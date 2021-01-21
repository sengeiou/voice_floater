package com.gydx.service;

import com.gydx.entity.User;

import java.util.List;

public interface UserService {

    User findByOpenId(String openId);

    String login(User user) throws Exception;

    User findById(String userId);

    List<String> findAllId();
}
