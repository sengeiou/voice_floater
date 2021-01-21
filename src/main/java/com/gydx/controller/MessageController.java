package com.gydx.controller;

import com.alibaba.fastjson.JSONObject;
import com.gydx.dto.MessageReplyDTO;
import com.gydx.entity.Message;
import com.gydx.entity.Queue;
import com.gydx.exception.RabbitmqException;
import com.gydx.response.Response;
import com.gydx.service.MessageService;
import com.gydx.service.QueueService;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.RabbitmqUtil;
import com.gydx.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author 拽小白
 * @createTime 2020-11-01 18:38
 * @description
 */
@RestController
@RequestMapping("message")
@Api(tags = "消息接口")
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${message.queueNamePrefix}")
    private String messageQueueNamePrefix;
    @Value("${recording.redisSetName}")
    private String recordingRedisSetName;
    @Value("${message.redisSetName}")
    private String messageRedisSetName;

    @Resource
    private RabbitmqUtil rabbitmqUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MessageService messageService;
    @Resource
    private QueueService queueService;

    @PostMapping("reply")
    @ApiOperation(value = "用户回复用户", httpMethod = "POST")
    public JSONObject reply(@RequestBody MessageReplyDTO messageReplyDTO) {
        Message message = messageService.replyComplete(messageReplyDTO);
        System.out.println("message = " + message);

        // 如果队列名不存在则将队列名存入数据库
        String queueName = message.getQueue();
        if (queueService.findByName(queueName) == null) {
            Queue queue = new Queue(null, queueName, null, null, message.getUserToId(), null);
            queueService.add(queue);
        }
        try {
            rabbitmqUtil.sendMsg(message);
//            redisUtil.sset(queueName, CommonUtil.objectToJson(message));

            return Response.success("已存放进消息队列", queueName);
        } catch (RabbitmqException rabbitmqException) {
            log.error(rabbitmqException.getMessage());
            return Response.fail(rabbitmqException.getMessage());
        } finally {
            // 将被回复的漂流瓶id重新放入redis中
            redisUtil.sset(recordingRedisSetName, messageReplyDTO.getRecordingId());
        }
    }

    @GetMapping("getMessageCount/{userId}")
    @ApiOperation(value = "获取该用户的系统通知未读数量", httpMethod = "GET")
    public JSONObject getMessageCount(@PathVariable String userId) {
        String queueName = messageQueueNamePrefix + userId;
        try {
            int messageCount = rabbitmqUtil.getMessageCount(queueName);
            return Response.success("获取成功", messageCount);
        } catch (RabbitmqException rabbitmqException) {
            log.error(rabbitmqException.getMessage());
            return Response.fail(rabbitmqException.getMessage());
        }
    }

    @GetMapping("getAllMessageList/{userId}")
    @ApiOperation(value = "获取用户所有的消息", httpMethod = "GET")
    @SuppressWarnings("unchecked")
    public JSONObject getAllMessageList(@PathVariable String userId) {
        String queueName = messageQueueNamePrefix + userId;
        try {
            Message message;
            List<Message> messageList = new ArrayList<>(100);
            List<Message> messageList1 = rabbitmqUtil.getMessageList(queueName);

            System.out.println("消息队列里的消息数：" + messageList1.size());

            for (Message message1 : messageList1) {
                System.out.println("-----------------------");
                System.out.println("message1 = " + message1);
            }

            Set<String> messages = (Set<String>) redisUtil.sGet(queueName);
            List<Message> messageList2 = new ArrayList<>(messages.size());
            for (String m : messages) {
                message = CommonUtil.jsonToObject(m, Message.class);
                messageList2.add(message);
            }
            messages = (Set<String>) redisUtil.sGet(messageRedisSetName + userId);
            List<Message> messageList3 = new ArrayList<>(messages.size());
            for (String m : messages) {
                message = CommonUtil.jsonToObject(m, Message.class);
                messageList3.add(message);
            }
            messageList.addAll(messageList1);
            messageList.addAll(messageList2);
            messageList.addAll(messageList3);

            for (Message message1 : messageList) {
                System.out.println("======================");
                System.out.println("message1 = " + message1);
            }

            // 消息队列读了的消息放到redis里
            for (Message m : messageList1) {
                redisUtil.sset(queueName, CommonUtil.objectToJson(m));
            }

            return Response.success("获取成功", messageList);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail("获取失败");
        }
    }

    @GetMapping("getNewMessageList/{userId}")
    @ApiOperation(value = "获取该用户的未读消息列表", httpMethod = "GET")
    public JSONObject getNewMessageList(@PathVariable String userId) {
        String queueName = messageQueueNamePrefix + userId;
        try {
            List<Message> messageList = rabbitmqUtil.getMessageList(queueName);
            // 消息队列读了的消息放到redis里
            for (Message message : messageList) {
                redisUtil.sset(queueName, CommonUtil.objectToJson(message));
            }
            return Response.success("获取成功", messageList);
        } catch (RabbitmqException rabbitmqException) {
            log.error(rabbitmqException.getMessage());
            return Response.fail(rabbitmqException.getMessage());
        }
    }

    /*@GetMapping("getOldMessageList/{userId}")
    @ApiOperation(value = "获取用户以前的消息", httpMethod = "GET")
    @SuppressWarnings("unchecked")
    public JSONObject getOldMessageList(@PathVariable String userId) {
        String queueName = messageQueueNamePrefix + userId;
        Set<String> objects = (Set<String>) redisUtil.sGet(queueName);
        List<Message> messages = new ArrayList<>((int) redisUtil.sgetSetSize(queueName));
        for (String o : objects) {
            Message message = CommonUtil.jsonToObject(o, Message.class);
            messages.add(message);
        }
        return Response.success("获取成功", messages);
    }*/

}
