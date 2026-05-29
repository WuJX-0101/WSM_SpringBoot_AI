package com.wms.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.SysUserMapper;
import com.wms.dao.mapper.SysUserRoleMapper;
import com.wms.model.dto.EmployeeDTO;
import com.wms.model.dto.EmployeeUpdateDTO;
import com.wms.model.dto.LoginDTO;
import com.wms.model.dto.RegisterDTO;
import com.wms.model.entity.SysUser;
import com.wms.model.entity.SysUserRole;
import com.wms.model.vo.LoginVO;
import com.wms.model.vo.UserVO;
import com.wms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 登录失败计数Key前缀 */
    private static final String LOGIN_FAIL_KEY = "login:fail:";
    /** 注册限流Key前缀 */
    private static final String REGISTER_LIMIT_KEY = "register:limit:";
    /** 最大登录失败次数 */
    private static final int MAX_LOGIN_FAIL = 5;
    /** 登录失败锁定时间（分钟） */
    private static final int LOGIN_LOCK_MINUTES = 15;
    /** 每小时最大注册次数 */
    private static final int MAX_REGISTER_PER_HOUR = 3;

    /**
     * 用户登录
     *
     * 流程:
     * 1. 检查账号是否被锁定（连续5次失败锁定15分钟）
     * 2. 根据用户名查询用户（排除已删除的用户）
     * 3. 验证用户状态（是否被禁用）
     * 4. 使用BCrypt验证密码
     * 5. 清除登录失败计数
     * 6. 调用Sa-Token生成Token
     * 7. 查询用户角色和权限
     * 8. 构建登录响应（包含Token和用户信息）
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String failKey = LOGIN_FAIL_KEY + username;

        // 1. 检查账号是否被锁定
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        if (failCountStr != null) {
            int failCount = Integer.parseInt(failCountStr);
            if (failCount >= MAX_LOGIN_FAIL) {
                throw new BusinessException("账号已锁定，请" + LOGIN_LOCK_MINUTES + "分钟后重试");
            }
        }

        // 2. 查询用户（LambdaQueryWrapper是MyBatis-Plus的条件构造器）
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getIsDeleted, 0)  // 逻辑删除过滤
        );

        if (user == null) {
            incrementLoginFail(failKey);
            throw new BusinessException("用户不存在");
        }

        // 3. 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 4. BCrypt密码验证（passwordEncoder.matches会自动处理盐值）
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            incrementLoginFail(failKey);
            throw new BusinessException("密码错误");
        }

        // 5. 登录成功，清除失败计数
        stringRedisTemplate.delete(failKey);

        // 6. Sa-Token登录（生成Token并存储到Redis）
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 7. 查询用户角色和权限（用于前端按钮级别权限控制）
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = sysUserMapper.selectPermissionsByUserId(user.getId());

        // 8. 构建用户信息VO
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .gmtCreate(user.getGmtCreate())
                .roles(roles)
                .permissions(permissions)
                .build();

        // 9. 构建登录响应（Token + 用户信息）
        return LoginVO.builder()
                .accessToken(tokenInfo.getTokenValue())
                .tokenType("Bearer")
                .expiresIn(tokenInfo.getTokenTimeout())
                .userInfo(userVO)
                .build();
    }

    /**
     * 增加登录失败计数
     *
     * @param failKey Redis Key
     */
    private void incrementLoginFail(String failKey) {
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            // 首次失败，设置过期时间
            stringRedisTemplate.expire(failKey, LOGIN_LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * 用户注册
     *
     * 流程:
     * 1. 检查注册限流（同一IP每小时最多注册3次）
     * 2. 检查用户名是否已存在
     * 3. 使用BCrypt加密密码（自动加盐）
     * 4. 插入用户记录
     *
     * @Transactional(rollbackFor = Exception.class) 表示任何异常都回滚事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 1. 检查注册限流（使用用户名作为限流Key，简化实现）
        String limitKey = REGISTER_LIMIT_KEY + registerDTO.getUsername();
        String countStr = stringRedisTemplate.opsForValue().get(limitKey);
        if (countStr != null) {
            int count = Integer.parseInt(countStr);
            if (count >= MAX_REGISTER_PER_HOUR) {
                throw new BusinessException("注册次数过多，请稍后再试");
            }
        }

        // 2. 检查用户名唯一性
        SysUser existUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, registerDTO.getUsername())
        );

        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        // BCrypt加密密码（每次加密结果不同，因为盐值随机）
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);  // 默认启用状态

        // 4. 插入数据库
        sysUserMapper.insert(user);

        // 5. 增加注册计数
        Long newCount = stringRedisTemplate.opsForValue().increment(limitKey);
        if (newCount != null && newCount == 1) {
            stringRedisTemplate.expire(limitKey, 1, TimeUnit.HOURS);
        }

        log.info("用户注册成功: {}", user.getUsername());
    }

    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> roles = sysUserMapper.selectRoleCodesByUserId(userId);
        List<String> permissions = sysUserMapper.selectPermissionsByUserId(userId);

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .gmtCreate(user.getGmtCreate())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
        log.info("用户退出登录");
    }

    /**
     * 员工列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @param keyword 搜索关键词（用户名/昵称）
     */
    @Override
    public PageResult<UserVO> listEmployees(int page, int size, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(SysUser::getUsername, keyword)
                .or()
                .like(SysUser::getNickname, keyword)
            );
        }
        
        wrapper.orderByDesc(SysUser::getGmtCreate);
        
        Page<SysUser> result = sysUserMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 转换为UserVO
        List<UserVO> userVOList = result.getRecords().stream().map(user -> {
            List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
            return UserVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .avatar(user.getAvatar())
                    .status(user.getStatus())
                    .gmtCreate(user.getGmtCreate())
                    .roles(roles)
                    .build();
        }).toList();
        
        return new PageResult<>(userVOList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 创建员工账户
     * 
     * 流程:
     * 1. 检查用户名是否已存在
     * 2. 使用BCrypt加密密码
     * 3. 创建用户记录
     * 4. 分配角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createEmployee(EmployeeDTO dto) {
        // 1. 检查用户名唯一性
        SysUser existUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );

        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);  // 默认启用状态

        // 3. 插入数据库
        sysUserMapper.insert(user);

        // 4. 分配角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }

        log.info("员工账户创建成功: {}", user.getUsername());
    }

    /**
     * 修改员工信息
     * 
     * 流程:
     * 1. 检查员工是否存在
     * 2. 更新员工基本信息
     * 3. 删除原有角色关联
     * 4. 重新分配角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(Long id, EmployeeUpdateDTO dto) {
        // 1. 检查员工是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("员工不存在");
        }

        // 2. 更新员工基本信息
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        sysUserMapper.updateById(user);

        // 3. 删除原有角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );

        // 4. 重新分配角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }

        log.info("员工信息修改成功: {}", user.getUsername());
    }

    /**
     * 删除员工（逻辑删除）
     * 
     * 流程:
     * 1. 检查员工是否存在
     * 2. 逻辑删除员工记录
     * 3. 删除角色关联
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long id) {
        // 1. 检查员工是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("员工不存在");
        }

        // 2. 逻辑删除员工记录
        sysUserMapper.deleteById(id);

        // 3. 删除角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );

        log.info("员工删除成功: {}", user.getUsername());
    }
}
