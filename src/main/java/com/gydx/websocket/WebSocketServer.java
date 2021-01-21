package com.gydx.websocket;

import com.gydx.exception.WebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于聊天或语音识别用
 *
 * @author 拽小白
 */
@ServerEndpoint("/imServer/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 用来记录当前在线连接数
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
            addOnlineCount();
        }

        log.info("用户连接：{}，当前在线人数为：{}", userId, getOnlineCount());

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            subOnlineCount();
        }
        log.info("用户退出：{}，当前在线人数为：{}", userId, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户：{}，消息：{}", userId, message);
        // TODO
    }

    @OnError
    public void onError(Session session, Throwable error) throws WebSocketException {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error("用户：{}，错误：{}", this.userId, error.getMessage());
        throw new WebSocketException("连接失败");
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException, WebSocketException {
        if (!StringUtils.isEmpty(userId) && webSocketMap.containsKey(userId)) {
            log.info("发送消息到：{}，消息：{}", userId, message);
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户：{} 不在线", userId);
            throw new WebSocketException("用户不在线");
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
