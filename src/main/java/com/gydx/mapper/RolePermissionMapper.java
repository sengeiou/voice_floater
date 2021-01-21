package com.gydx.mapper;

import com.gydx.entity.Permission;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 15:17
 * @description
 */
public interface RolePermissionMapper {
    List<Permission> findPermissionByRoleId(Integer roleId);
}
