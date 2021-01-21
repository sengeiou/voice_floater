package com.gydx;

import com.gydx.entity.Message;
import com.gydx.entity.Permission;
import com.gydx.entity.Queue;
import com.gydx.entity.User;
import com.gydx.exception.RabbitmqException;
import com.gydx.mapper.MessageMapper;
import com.gydx.mapper.PermissionMapper;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.RabbitmqUtil;

import com.gydx.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 拽小白
 * @createTime 2020-11-07 10:26
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoTest {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private PermissionMapper permissionMapper;

    @Test
    public void test9() {
        List<Permission> permissions = permissionMapper.getPermissionByParentId(0);
        for (Permission permission : permissions) {
            System.out.println(permission);
        }
    }

    @Test
    public void test8() {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("5");

        Queue queue = new Queue(null, "123", "1", null, "2", null);

        System.out.println(ids.contains(queue.getUserToId()));
    }

    @Test
    public void test7() {
        List<Message> messages = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Message message = new Message(null, String.valueOf(i + 1), String.valueOf(i + 2), null, "123", "chat12", "2020-09-01 10:23:11");
            System.out.println(message);
            messages.add(message);
        }
        messageMapper.addBatch(messages);
    }

    @Test
    public void test6() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

    @Test
    public void contextLoader() {
        Set<String> message1 = (Set<String>) redisUtil.sGet("message1");
        for (String s : message1) {
            System.out.println(s);
            Message message = CommonUtil.jsonToObject(s, Message.class);
            System.out.println(message);
        }
    }

    @Test
    public void test4() throws IOException, ClassNotFoundException {
        String s = "rO0ABXNyABdjb20uZ3lkeC5lbnRpdHkuTWVzc2FnZe9mIhh1vOz2AgAHTAAHY29udGVudHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wAAmlkdAATTGphdmEvbGFu" +
                "Zy9JbnRlZ2VyO0wABXF1ZXVlcQB+AAFMAAtyZWNvcmRpbmdJZHEAfgABTAAIc2VuZFRpbWVxAH4AAUwACnVzZXJGcm9tSWRxAH4AAUwACHVzZXJUb0lkcQB+" +
                "AAF4cHQAB2hlbGxvIDBzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAA" +
                "AXQABnF1ZXVlMXQAAjEzcHQAATF0AAEy";
        byte[] bytes = s.getBytes();
        Message message = CommonUtil.deserialization(bytes, Message.class);
        System.out.println(message);
    }

    @Resource
    private RabbitmqUtil rabbitmqUtil;
    @Resource
    private RedisUtil redisUtil;

    @Test
    public void test5() {
        redisUtil.sset("test", "1", "2", "3", "4", "5", "6");

        Set<?> test = redisUtil.sGet("test");
        System.out.println(test.size());
        Set<?> test2 = redisUtil.sGet("test");
        System.out.println(test2.size());
    }

    @Test
    public void test2() throws RabbitmqException {
        for (int i = 1; i <= 10; i++) {
            String content = "hello" + i;
            System.out.println(content);
            Message message = new Message(1, "1", "2", String.valueOf(i), content, "queue1", null);
            rabbitmqUtil.sendMsg(message);
        }
        int messageCount = rabbitmqUtil.getMessageCount("queue1");
        System.out.println(messageCount);

        /*List<Message> messageList = rabbitmqUtil.getMessageList("queue1");
        for (Message message : messageList) {
            System.out.println(message);
        }*/
    }

    @Test
    public void test3() throws RabbitmqException {
        List<Message> messageList = rabbitmqUtil.getMessageList("message1");
        for (Message message : messageList) {
            System.out.println(message);
        }
    }

    @Test
    public void test1() {
        String template = "{toUser}:\n\t{fromUser}回复了你: {content}";
        System.out.println("template = " + template);

        User toUser = new User();
        toUser.setId(1);
        toUser.setNickName("张三");
        User fromUser = new User();
        fromUser.setNickName("李四");
        String content = "你好";

        Map<String, String> map = new HashMap<>();
        map.put("toUser", toUser.getNickName());
        map.put("fromUser", fromUser.getNickName());
        map.put("content", content);

        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String group = matcher.group();
            System.out.println("group = " + group);
            String key = group.substring(1, group.length() - 1);
            System.out.println("key = " + key);
            if (map.containsKey(key)) {
                String value = map.get(key);
                template = StringUtils.replace(template, group, value);
            }
        }
        System.out.println("template = " + template);
    }

}
