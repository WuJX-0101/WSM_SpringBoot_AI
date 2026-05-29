package com.wms.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.SysUserMapper;
import com.wms.dao.mapper.SysUserRoleMapper;
import com.wms.model.dto.LoginDTO;
import com.wms.model.dto.RegisterDTO;
import com.wms.model.entity.SysUser;
import com.wms.model.vo.LoginVO;
import com.wms.model.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务单元测试")
class AuthServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword(new BCryptPasswordEncoder().encode("123456"));
        testUser.setNickname("管理员");
        testUser.setStatus(1);
        testUser.setIsDeleted(0);

        loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");

        registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("新用户");
    }

    @Test
    @DisplayName("登录成功 - 正常登录流程")
    void login_Success() {
        // Given
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login:fail:admin")).thenReturn(null);

        // Mock Sa-Token
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            SaTokenInfo tokenInfo = new SaTokenInfo();
            tokenInfo.setTokenValue("test-token-123");
            tokenInfo.setTokenTimeout(86400L);
            stpUtilMock.when(() -> StpUtil.login(anyLong())).then(invocation -> null);
            stpUtilMock.when(StpUtil::getTokenInfo).thenReturn(tokenInfo);

            when(sysUserMapper.selectRoleCodesByUserId(anyLong())).thenReturn(Arrays.asList("SUPER_ADMIN"));
            when(sysUserMapper.selectPermissionsByUserId(anyLong())).thenReturn(Arrays.asList("*"));

            // When
            LoginVO result = authService.login(loginDTO);

            // Then
            assertNotNull(result);
            assertEquals("test-token-123", result.getAccessToken());
            assertEquals("admin", result.getUserInfo().getUsername());
            verify(stringRedisTemplate).delete("login:fail:admin");
        }
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_Fail_UserNotFound() {
        // Given
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login:fail:admin")).thenReturn(null);
        when(valueOperations.increment("login:fail:admin")).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });
        assertEquals("用户不存在", exception.getMessage());
        verify(stringRedisTemplate).expire("login:fail:admin", 15, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_Fail_WrongPassword() {
        // Given
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login:fail:admin")).thenReturn(null);
        when(valueOperations.increment("login:fail:admin")).thenReturn(1L);

        loginDTO.setPassword("wrongpassword");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 账号已锁定")
    void login_Fail_AccountLocked() {
        // Given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login:fail:admin")).thenReturn("5");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });
        assertTrue(exception.getMessage().contains("账号已锁定"));
    }

    @Test
    @DisplayName("登录失败 - 用户被禁用")
    void login_Fail_UserDisabled() {
        // Given
        testUser.setStatus(0);
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login:fail:admin")).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });
        assertEquals("用户已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("注册成功 - 正常注册流程")
    void register_Success() {
        // Given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("register:limit:newuser")).thenReturn(null);
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(valueOperations.increment("register:limit:newuser")).thenReturn(1L);

        // When & Then
        assertDoesNotThrow(() -> authService.register(registerDTO));
        verify(sysUserMapper).insert(any(SysUser.class));
        verify(stringRedisTemplate).expire("register:limit:newuser", 1, TimeUnit.HOURS);
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_Fail_UsernameExists() {
        // Given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("register:limit:newuser")).thenReturn(null);
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(registerDTO);
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("注册失败 - 注册次数过多")
    void register_Fail_RateLimited() {
        // Given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("register:limit:newuser")).thenReturn("3");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(registerDTO);
        });
        assertEquals("注册次数过多，请稍后再试", exception.getMessage());
    }

    @Test
    @DisplayName("获取当前用户信息 - 成功")
    void getCurrentUserInfo_Success() {
        // Given
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(sysUserMapper.selectById(1L)).thenReturn(testUser);
            when(sysUserMapper.selectRoleCodesByUserId(1L)).thenReturn(Arrays.asList("SUPER_ADMIN"));
            when(sysUserMapper.selectPermissionsByUserId(1L)).thenReturn(Arrays.asList("*"));

            // When
            UserVO result = authService.getCurrentUserInfo();

            // Then
            assertNotNull(result);
            assertEquals("admin", result.getUsername());
            assertEquals("管理员", result.getNickname());
        }
    }

    @Test
    @DisplayName("退出登录 - 成功")
    void logout_Success() {
        // Given
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::logout).then(invocation -> null);

            // When & Then
            assertDoesNotThrow(() -> authService.logout());
        }
    }
}
