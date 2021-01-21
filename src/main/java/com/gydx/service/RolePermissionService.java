package com.gydx.service;

import com.gydx.entity.Permission;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 15:13
 * @description
 */
public interface RolePermissionService {

    /**
     * 根据角色id获取该角色拥有的所有权限
     * @param roleId 角色id
     * @return 权限列表
     */
    List<Permission> findPermissionByRoleId(Integer roleId);
}
