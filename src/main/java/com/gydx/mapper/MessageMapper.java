package com.gydx.mapper;

import com.gydx.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-12 12:16
 * @description
 */
public interface MessageMapper {
    /**
     * 批量插入消息
     * @param messages 消息列表
     */
    void addBatch(@Param("messages") List<Message> messages);

    List<Message> findBUserToId(String userToId);
}
