package com.gydx.service.admin.impl;

import com.gydx.dto.admin.UserAdminLoginDTO;
import com.gydx.entity.User;
import com.gydx.mapper.admin.UserAdminMapper;
import com.gydx.service.admin.UserAdminService;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-13 20:04
 * @description
 */
@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Resource
    private UserAdminMapper userAdminMapper;
    @Resource
    private JwtUtil jwtUtil;

    @Override
    public List<User> findAll() {
        return userAdminMapper.findAll();
    }

    @Override
    public String login(UserAdminLoginDTO userAdminLoginDTO) {
        User user = userAdminMapper.findByNickName(userAdminLoginDTO.getNickName());
        // 用户名与密码正确
        if (userAdminLoginDTO.getPassword().equals(user.getPassword())) {
            // 更新登录时间
            user.setLastTime(new Timestamp(System.currentTimeMillis()));
            userAdminMapper.update(user);
            // 返回生成的token
            return jwtUtil.sign(user);
        }
        // 没有对应用户，则返回null
        return null;
    }
}
