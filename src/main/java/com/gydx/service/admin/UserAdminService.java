package com.gydx.service.admin;

import com.gydx.dto.admin.UserAdminLoginDTO;
import com.gydx.entity.User;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-13 20:04
 * @description
 */
public interface UserAdminService {
    /**
     * 查询所有的用户
     * @return 返回查询出的用户列表
     */
    List<User> findAll();

    String login(UserAdminLoginDTO userAdminLoginDTO);
}
