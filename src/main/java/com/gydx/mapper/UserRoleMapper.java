package com.gydx.mapper;

import com.gydx.entity.Role;
import com.gydx.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-10-30 12:28
 * @description
 */
public interface UserRoleMapper {

    /**
     * 根据userId查询出该用户拥有的所有角色
     * @param userId userId
     * @return 该用户拥有的所有角色
     */
    List<Role> findAllRoleByUserId(Integer userId);

    /**
     * 根据roleId查询出拥有该角色的所有用户
     * @param roleId roleId
     * @return 拥有该角色的所有用户
     */
    List<User> findAllUserByRoleId(Integer roleId);

    /**
     * 添加一个用户与角色的映射关系
     * @param userId 用户id
     * @param roleId 角色id
     */
    void add(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
