package com.gydx.service;

import com.gydx.dto.MessageReplyDTO;
import com.gydx.entity.Message;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-12 12:09
 * @description
 */
public interface MessageService {
    /**
     * 批量存入
     * @param messages 要存入的数据
     */
    void addBatch(List<Message> messages);

    /**
     * 将回复的消息填充完整
     * @param messageReplyDTO 从前端传来的基础信息
     * @return 返回将基础信息填充完整后的完整信息
     */
    Message replyComplete(MessageReplyDTO messageReplyDTO);

    List<Message> findByUserToId(String userId);
}
