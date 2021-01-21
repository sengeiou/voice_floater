package com.gydx.utils;

import com.gydx.entity.Message;
import com.gydx.exception.RabbitmqException;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author 拽小白
 * @createTime 2020-10-28 21:08
 * @description
 */
@Component("rabbitmqUtil")
public class RabbitmqUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ConnectionFactory connectionFactory;

    /**
     * 发送指定消息到指定消息队列
     * @param message 指定消息
     * @throws RabbitmqException
     */
    public void sendMsg(Message message) throws RabbitmqException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            System.out.println("message.getQueue() = " + message.getQueue());

            channel.queueDeclare(message.getQueue(), true, false, false, null);
        } catch (TimeoutException e) {
            log.error("创建连接失败");
        } catch (IOException exception) {
            log.error("队列声明失败");
        }

        byte[] messageByteData;
        try {
            messageByteData = CommonUtil.serialization(message);
        } catch (IOException ioException) {
            throw new RabbitmqException("message序列化失败");
        }
        try {
            if (channel == null) {
                throw new RabbitmqException("队列创建失败");
            }
            channel.basicPublish("", message.getQueue(), null, messageByteData);

        } catch (IOException ioException) {
            log.error(ioException.getMessage());
            throw new RabbitmqException("消息发布失败");
        }

        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException exception) {
            log.error(exception.getMessage());
            throw new RabbitmqException("通道或连接关闭失败");
        }

    }

    /**
     * 获取该队列堆积的所有消息
     * @param queueName 队列名
     * @return 消息集合
     * @throws RabbitmqException
     */
    public List<Message> getMessageList(String queueName) throws RabbitmqException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
        } catch (TimeoutException e) {
            log.error("创建连接失败");
        } catch (IOException exception) {
            log.error("队列声明失败");
        }
        List<Message> messages = new ArrayList<>();
        try {
            if (channel == null) {
                throw new RabbitmqException("获取队列消息失败");
            }
            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
                @SneakyThrows
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        Message message = CommonUtil.deserialization(body, Message.class);
                        System.out.println(message);
                        messages.add(message);
                    } catch (Exception e) {
                        log.error("反序列化出错：{}", e.getMessage());
                        throw new RabbitmqException("反序列化出错");
                    }
                }
            });
        } catch (RabbitmqException rabbitmqException) {
            log.error(rabbitmqException.getMessage());
            throw new RabbitmqException("获取队列消息失败");
        } catch (IOException ioException) {
            log.error("获取消息队列中的消息失败：{}", ioException.getMessage());
            throw new RabbitmqException("获取队列消息失败");
        /*} catch (ClassNotFoundException e) {
            e.printStackTrace();*/
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException | TimeoutException e) {
                log.error("关闭channel和connection失败：{}", e.getMessage());
            }
        }
        return messages;
    }

    /**
     * 获取队列里堆积的消息数量
     *
     * @param queueName 队列名
     * @return 消息数量
     */
    public int getMessageCount(String queueName) throws RabbitmqException {
        try {
            return connectionFactory.newConnection().createChannel().queueDeclarePassive(queueName).getMessageCount();
        } catch (Exception e) {
            log.error("获取队列消息数量失败：{}", e.getMessage());
            throw new RabbitmqException("获取队列消息数量失败");
        }
    }
}
