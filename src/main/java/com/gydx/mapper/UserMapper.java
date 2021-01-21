package com.gydx.mapper;


import com.gydx.entity.User;

import java.util.List;

/**
 * @author 拽小白
 */
public interface UserMapper {

    User findById(String userId);

    User findByOpenId(String openId);

    void update(User user);

    void add(User user);

    void setUTF();

    List<String> findAllId();
}
