package com.gydx.async;

import com.alibaba.fastjson.JSONObject;
import com.gydx.utils.SpringContextUtil;
import com.gydx.utils.TencentCloudUtil;
import com.gydx.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 拽小白
 * @createTime 2020-11-01 14:04
 * @description 录音文件类的异步方法
 */
public class RecordingMethods {

    /**
     * 根据taskId异步请求腾讯云接口获取语音识别的结果并通过websocket发送给前端
     * @param taskId
     */
    @Async
    public void getRecognitionAndSend(Integer taskId, String userId) {
        System.out.println("userId = " + userId);
        while(true) {
            try {
                String res = ((TencentCloudUtil) SpringContextUtil.getBean("tencentCloudUtil")).result(taskId);
                System.out.println(taskId);
                JSONObject resJson = JSONObject.parseObject(res);
                JSONObject data = resJson.getJSONObject("Data");
                Integer status = data.getInteger("Status");
                if (status == 3) {
                    WebSocketServer.sendInfo("语音识别失败", userId);
                    break;
                } else if (status == 2) {
                    String result = data.getString("Result");
                    WebSocketServer.sendInfo(result, userId);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
