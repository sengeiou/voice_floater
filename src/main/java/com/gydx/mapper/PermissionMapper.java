package com.gydx.mapper;

import com.gydx.entity.Permission;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-29 13:10
 * @description
 */
public interface PermissionMapper {

    /**
     * 获取所有的目录
     * @param parentId 父id
     * @return 返回所有的目录
     */
    List<Permission> getPermissionByParentId(Integer parentId);
}
