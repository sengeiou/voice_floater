package com.gydx.service.impl;

import com.gydx.entity.Queue;
import com.gydx.mapper.QueueMapper;
import com.gydx.service.QueueService;
import com.gydx.utils.EmojiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-15 10:10
 * @description
 */
@Service("queueService")
@Slf4j
public class QueueServiceImpl implements QueueService {

    @Resource
    private QueueMapper queueMapper;

    @Override
    public void add(Queue queue) {
        queueMapper.add(queue);
    }

    @Override
    public Queue findByName(String queueName) {
        return queueMapper.findByName(queueName);
    }

    @Override
    public List<Queue> findAll() {
        return queueMapper.findAll();
    }

    @Override
    public List<Queue> findAllChatByUserFromId(String userFromId) {
        return queueMapper.findAllChatByUserFromId(userFromId);
    }

    @Override
    public List<Queue> findAllChatWithUserByUserId(String userId) {
        List<Queue> queueList = queueMapper.findAllChatWithUserByUserId(userId);
        List<String> userToIds = new ArrayList<>(queueList.size());
        List<Queue> queues = new ArrayList<>(queueList.size());

        for (Queue queue : queueList) {
            if (userId.equals(queue.getUserFromId())) {
                // 如果还没有
                if (!userToIds.contains(queue.getUserToId())) {
                    queue.getUserFrom().setNickName(EmojiUtil.htmlToEmoji(queue.getUserFrom().getNickName()));
                    queue.getUserTo().setNickName(EmojiUtil.htmlToEmoji(queue.getUserTo().getNickName()));
                    queues.add(queue);
                    userToIds.add(queue.getUserToId());
                }
            } else {
                if (!userToIds.contains(queue.getUserFromId())) {
                    queue.getUserFrom().setNickName(EmojiUtil.htmlToEmoji(queue.getUserFrom().getNickName()));
                    queue.getUserTo().setNickName(EmojiUtil.htmlToEmoji(queue.getUserTo().getNickName()));
                    queues.add(queue);
                    userToIds.add(queue.getUserFromId());
                }
            }
        }
        return queues;
    }
}
