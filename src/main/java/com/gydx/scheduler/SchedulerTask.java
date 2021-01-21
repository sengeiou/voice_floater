package com.gydx.scheduler;

import com.gydx.entity.Message;
import com.gydx.entity.Queue;
import com.gydx.service.MessageService;
import com.gydx.service.QueueService;
import com.gydx.service.UserService;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author 拽小白
 * @createTime 2020-11-12 10:49
 * @description
 */
@Component
public class SchedulerTask {

    private static final String QUEUE_PREFIX = "queue";

    @Value("${message.redisSetName}")
    private String messageRedisSetName;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MessageService messageService;
    @Resource
    private UserService userService;

    /**
     * 每24小时将redis里存放的系统消息持久化到数据库中
     * TODO 待优化
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    @SuppressWarnings("unchecked")
    private void redisTask(){
        // 假设今天产生了1000条消息，降低扩容概率
        List<Message> messages = new ArrayList<>(1000);
        // 获得所有的userId
        List<String> userIdList = userService.findAllId();
        // 循环获取今天产生的所有消息
        for (String userId : userIdList) {
            String queueName = QUEUE_PREFIX + userId;
            Set<String> messageStrings = (Set<String>) redisUtil.sGet(queueName);
            for (String messageString : messageStrings) {
                Message message = CommonUtil.jsonToObject(messageString, Message.class);
                messages.add(message);
            }
            // 将该用户缓存的消息存到全部消息的redis中
            String messageName = messageRedisSetName + userId;
            redisUtil.sUnion(messageName, queueName);
            // 将今天临时的消息set删除
            redisUtil.del(queueName);
        }
        // 将今天产生的消息批量插入mysql中
        messageService.addBatch(messages);
    }

}
