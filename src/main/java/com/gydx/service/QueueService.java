package com.gydx.service;

import com.gydx.entity.Queue;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-15 10:09
 * @description
 */
public interface QueueService {
    void add(Queue queue);

    Queue findByName(String queueName);

    List<Queue> findAll();

    List<Queue> findAllChatByUserFromId(String userFromId);

    List<Queue> findAllChatWithUserByUserId(String userId);
}
