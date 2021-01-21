package com.gydx.service.impl;

import com.gydx.entity.Permission;
import com.gydx.mapper.PermissionMapper;
import com.gydx.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 15:11
 * @description
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getAllMenu() {
        return permissionMapper.getPermissionByParentId(0);
    }

}
