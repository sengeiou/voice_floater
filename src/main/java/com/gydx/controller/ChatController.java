package com.gydx.controller;

import com.alibaba.fastjson.JSONObject;
import com.gydx.dto.ChatSendDTO;
import com.gydx.entity.Message;
import com.gydx.entity.Queue;
import com.gydx.exception.RabbitmqException;
import com.gydx.exception.WebSocketException;
import com.gydx.response.Response;
import com.gydx.service.QueueService;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.RabbitmqUtil;
import com.gydx.utils.RedisUtil;
import com.gydx.websocket.WebSocketServer;
import com.gydx.websocket.WebSocketServer2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 拽小白
 * @createTime 2020-11-16 20:01
 * @description
 */
@RestController
@RequestMapping("chat")
@Api(tags = "聊天接口")
@Slf4j
public class ChatController {

    @Value("${chat.queueNamePrefix}")
    private String chatQueueNamePrefix;
    @Value("${chat.redisSetName}")
    private String chatRedisName;

    @Resource
    private QueueService queueService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RabbitmqUtil rabbitmqUtil;

    @PostMapping("send")
    @ApiOperation(value = "发送消息", httpMethod = "POST")
    public JSONObject send(@RequestBody ChatSendDTO chatSendDTO) {
        Message message = new Message(chatSendDTO);
        String messageJson = CommonUtil.objectToJson(message);

        // 如果数据库中队列不存在则创建并添加
        if (queueService.findByName(message.getQueue()) == null) {
            Queue queue = new Queue(null, message.getQueue(), chatSendDTO.getUserFromId(), null, chatSendDTO.getUserToId(), null);
            queueService.add(queue);
        }

        /*try {
            WebSocketServer.sendInfo(messageJson, message.getUserToId());
            redisUtil.sset(message.getQueue(), messageJson);
            return Response.success("发送成功");
        } catch (IOException | WebSocketException e) {
            log.error("用户不在线");
        }*/

        try {
            WebSocketServer2.sendInfo(messageJson, "0" + message.getUserToId() + message.getUserFromId());
            redisUtil.sset(message.getQueue(), messageJson);
            return Response.success("发送成功");
        } catch (IOException | WebSocketException e) {
            log.error("用户不在线");
        }

        try {
            rabbitmqUtil.sendMsg(message);
//            redisUtil.sset(message.getQueue(), messageJson);
            return Response.success("发送到消息队列成功");
        } catch (RabbitmqException e) {
            log.error(e.getMessage());
        }
        return Response.fail("发送失败");
    }

    @GetMapping("getMessageList/{userId}")
    @ApiOperation(value = "获取对话列表", httpMethod = "GET")
    public JSONObject getMessageList(@PathVariable String userId) {
        List<Queue> queueList = queueService.findAllChatWithUserByUserId(userId);
        return Response.success("获取成功", queueList);
    }

    @GetMapping("getMessageCount/{userFromId}")
    @ApiOperation(value = "获取每个对话的未读消息数量", httpMethod = "GET")
    public JSONObject getMessageCount(@PathVariable String userFromId) {
        List<Queue> queueList = queueService.findAllChatByUserFromId(userFromId);
        // key：userToId，value：未读消息数，并设置总共有32条对话
        Map<String, Integer> messageCountMap = new HashMap<>(32);
        for (Queue queue : queueList) {
            System.out.println("queue = " + queue);
            int messageCount;
            try {
                messageCount = rabbitmqUtil.getMessageCount(queue.getQueueName());
            } catch (RabbitmqException rabbitmqException) {
                log.error(rabbitmqException.getMessage());
                // 如果获取未读消息出错，就将该消息对话的未读消息量设置为0
                messageCount = 0;
            }
            messageCountMap.put(queue.getQueueName(), messageCount);
        }
        return Response.success("查询成功", messageCountMap);
    }

    @GetMapping("getAllMessage/{userFromId}/{userToId}")
    @ApiOperation(value = "获取某个对话的所有消息", httpMethod = "GET")
    @SuppressWarnings("unchecked")
    public JSONObject getAllMessage(@PathVariable String userFromId, @PathVariable String userToId) {
        String queueName = chatQueueNamePrefix + userFromId + userToId;
        String queueName2 = chatQueueNamePrefix + userToId + userFromId;
        Message message;
        // 将每个消息列表的总消息量初始化为100条
        List<Message> messageList = new ArrayList<>(100);
        List<Message> messageListQueue, messageListQueue2;
        try {
            // 从消息队列中取出最新的消息
            messageListQueue = rabbitmqUtil.getMessageList(queueName);
            messageListQueue2 = rabbitmqUtil.getMessageList(queueName2);
            messageList.addAll(messageListQueue);
            messageList.addAll(messageListQueue2);
        } catch (RabbitmqException rabbitmqException) {
            log.error(rabbitmqException.getMessage());
            return Response.fail("获取失败");
        }
        // 从redis中取出今天临时缓存的所有消息
        LinkedHashSet<String> tempNew = (LinkedHashSet<String>) redisUtil.sGet(queueName);
        LinkedHashSet<String> tempNew2 = (LinkedHashSet<String>) redisUtil.sGet(queueName2);
        List<Message> messageListRedisNew = new ArrayList<>(tempNew.size());
        List<Message> messageListRedisNew2 = new ArrayList<>(tempNew2.size());
        for (String s : tempNew) {
            message = CommonUtil.jsonToObject(s, Message.class);
            messageListRedisNew.add(message);
        }
        for (String s : tempNew2) {
            message = CommonUtil.jsonToObject(s, Message.class);
            messageListRedisNew2.add(message);
        }
        messageList.addAll(messageListRedisNew);
        messageList.addAll(messageListRedisNew2);

        // 从redis中取出今天之前缓存的所有消息
        LinkedHashSet<String> tempOld = (LinkedHashSet<String>) redisUtil.sGet(chatRedisName + userFromId + userToId);
        LinkedHashSet<String> tempOld2 = (LinkedHashSet<String>) redisUtil.sGet(chatRedisName + userToId + userFromId);
        List<Message> messageListRedisOld = new ArrayList<>(tempOld.size());
        List<Message> messageListRedisOld2 = new ArrayList<>(tempOld2.size());
        for (String s : tempOld) {
            message = CommonUtil.jsonToObject(s, Message.class);
            messageListRedisOld.add(message);
        }
        for (String s : tempOld2) {
            message = CommonUtil.jsonToObject(s, Message.class);
            messageListRedisOld2.add(message);
        }
        messageList.addAll(messageListRedisOld);
        messageList.addAll(messageListRedisOld2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将消息根据时间前后进行排序
        messageList.sort((o1, o2) -> {
            try {
                return simpleDateFormat.parse(o1.getSendTime()).compareTo(simpleDateFormat.parse(o2.getSendTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        for (Message messageQueue : messageListQueue) {
            redisUtil.sset(queueName, CommonUtil.objectToJson(messageQueue));
        }
        for (Message messageQueue : messageListQueue2) {
            redisUtil.sset(queueName, CommonUtil.objectToJson(messageQueue));
        }

        for (Message message1 : messageList) {
            System.out.println("message1 = " + message1);
        }

        return Response.success("获取成功", messageList);
    }

}
