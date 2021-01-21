package com.gydx.service;

import com.gydx.entity.Permission;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 15:11
 * @description
 */
public interface PermissionService {
    /**
     * 获取所有菜单
     * @return 返回查询到的菜单
     */
    List<Permission> getAllMenu();
}
