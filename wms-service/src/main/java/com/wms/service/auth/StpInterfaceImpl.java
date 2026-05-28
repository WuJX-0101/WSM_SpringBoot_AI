package com.wms.service.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.wms.dao.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限认证接口实现
 * 
 * 功能：
 * - 查询用户拥有的权限列表
 * - 查询用户拥有的角色列表
 * 
 * Sa-Token 会自动调用此接口进行权限校验
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserMapper sysUserMapper;

    /**
     * 返回指定用户拥有的权限列表
     * 
     * @param loginId 用户ID
     * @param loginType 账号类型
     * @return 权限标识列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return sysUserMapper.selectPermissionsByUserId(userId);
    }

    /**
     * 返回指定用户拥有的角色列表
     * 
     * @param loginId 用户ID
     * @param loginType 账号类型
     * @return 角色标识列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return sysUserMapper.selectRoleCodesByUserId(userId);
    }
}
