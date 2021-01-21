package com.gydx.mapper;

import com.gydx.entity.Queue;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-15 10:15
 * @description
 */
public interface QueueMapper {
    void add(Queue queue);

    Queue findByName(String queueName);

    List<Queue> findAll();

    List<Queue> findAllChatByUserFromId(String userFromId);

    List<Queue> findAllChatWithUserByUserId(String userId);
}
