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
 * @author 拽小白
 * @createTime 2020-11-22 09:48
 * @description
 */
@ServerEndpoint("/imServer2/{idStr}")
@Component
@Slf4j
public class WebSocketServer2 {

    /**
     * 用来记录当前在线连接数
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
     */
    private static ConcurrentHashMap<String, WebSocketServer2> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String idStr;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("idStr") String idStr) {
        this.session = session;
        this.idStr = idStr;
        if (webSocketMap.containsKey(idStr)) {
            webSocketMap.remove(idStr);
            webSocketMap.put(idStr, this);
        } else {
            webSocketMap.put(idStr, this);
            addOnlineCount();
        }

        log.info("用户连接：{}，当前在线人数为：{}", idStr, getOnlineCount());

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(idStr)) {
            webSocketMap.remove(idStr);
            subOnlineCount();
        }
        log.info("用户退出：{}，当前在线人数为：{}", idStr, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户：{}，消息：{}", idStr, message);
        // TODO
    }

    @OnError
    public void onError(Session session, Throwable error) throws WebSocketException {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error("用户：{}，错误：{}", this.idStr, error.getMessage());
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
    public static void sendInfo(String message, @PathParam("idStr") String idStr) throws IOException, WebSocketException {
        if (!StringUtils.isEmpty(idStr) && webSocketMap.containsKey(idStr)) {
            log.info("发送消息到：{}，消息：{}", idStr, message);
            webSocketMap.get(idStr).sendMessage(message);
        } else {
            log.error("用户：{} 不在线", idStr);
            throw new WebSocketException("用户不在线");
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer2.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer2.onlineCount--;
    }

}
