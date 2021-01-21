package com.gydx.service.impl;

import com.gydx.constant.RoleConstant;
import com.gydx.entity.Message;
import com.gydx.entity.User;
import com.gydx.exception.RabbitmqException;
import com.gydx.exception.UserException;
import com.gydx.mapper.UserMapper;
import com.gydx.mapper.UserRoleMapper;
import com.gydx.service.UserService;
import com.gydx.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${message.queueNamePrefix}")
    private String messageQueueNamePrefix;
    @Value("${welcome}")
    private String welcome;

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RabbitmqUtil rabbitmqUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public User findByOpenId(String openId) {
        return userMapper.findByOpenId(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String login(User user) throws UserException {
        User u = userMapper.findByOpenId(user.getOpenId());

        // 用户不是第一次登录
        if (u != null) {
            if (u.getStatus() == 0) {
                user.setId(u.getId());
                user.setNickName(EmojiUtil.emojiToHtml(user.getNickName()));
                user.setRoles(u.getRoles());
                userMapper.update(user);
            } else {
                throw new UserException("用户被禁止登录！");
            }
        } else {  // 说明用户是第一次使用小程序
            user.setNickName(EmojiUtil.emojiToHtml(user.getNickName()));
            userMapper.add(user);
            // 建立新用户与角色的对应关系
            userRoleMapper.add(user.getId(), RoleConstant.ORDINARY_USER.getRoleId());
            // 将角色信息放入user
            user.setRoles(userRoleMapper.findAllRoleByUserId(user.getId()));

            // 第一次使用小程序建立一个系统通知的消息队列，并发送欢迎消息
            // 不异步发送消息保证在连接websocket时队列已经创建
            /*Message welcomeMessage = new Message(null, null, String.valueOf(user.getId()), null, welcome, messageQueueNamePrefix + user.getId(), CommonUtil.getNowTime());
            try {
                rabbitmqUtil.sendMsg(welcomeMessage);
                // 消息发送成功了再将消息存放到redis里
                redisUtil.sset(messageRedisSetName, CommonUtil.objectToJson(welcomeMessage));
            } catch (RabbitmqException rabbitmqException) {
                log.error(rabbitmqException.getMessage());
                // 消息重发 TODO
            }*/
        }
        // 返回jwt的token
        return jwtUtil.sign(user);
    }

    @Override
    public User findById(String userId) {
        return userMapper.findById(userId);
    }

    @Override
    public List<String> findAllId() {
        return userMapper.findAllId();
    }
}
