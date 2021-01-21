package com.gydx.mapper.admin;

import com.gydx.entity.User;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-13 20:12
 * @description
 */
public interface UserAdminMapper {

    /**
     * 查询所有用户
     * @return 返回查询到的用户列表
     */
    List<User> findAll();

    /**
     * 根据昵称查询用户
     * @param nickName 查询依据
     * @return 返回查询到的用户
     */
    User findByNickName(String nickName);

    /**
     * 根据id更新用户信息
     * @param user 更新后的用户信息
     */
    void update(User user);
}
