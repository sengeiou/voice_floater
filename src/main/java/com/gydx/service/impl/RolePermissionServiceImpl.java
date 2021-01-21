package com.gydx.service.impl;

import com.gydx.entity.Permission;
import com.gydx.mapper.RolePermissionMapper;
import com.gydx.service.RolePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 15:14
 * @description
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Permission> findPermissionByRoleId(Integer roleId) {
        return rolePermissionMapper.findPermissionByRoleId(roleId);
    }

}
