package com.gydx.utils;

import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 拽小白
 */
@Component("tencentCloudUtil")
public class TencentCloudUtil {

    @Value("${tx.secretId}")
    private String secretId;
    @Value("${tx.secretKey}")
    private String secretKey;
    @Value("${tx.requestUrl}")
    private String requestUrl;

    // 你自己的腾讯云用户名和密钥
    private final Credential cred = new Credential("", "");

    /**
     * 语音文件识别
     * @param url
     * @return
     */
    public Integer recognition(String url) {
        try{
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(requestUrl);

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            AsrClient client = new AsrClient(cred, "ap-shanghai", clientProfile);

            String params = "{\"EngineModelType\":\"16k_zh\",\"ChannelNum\":1,\"ResTextFormat\":0,\"SourceType\":0,\"Url\":\"" + url + "\"}";
            CreateRecTaskRequest req = CreateRecTaskRequest.fromJsonString(params, CreateRecTaskRequest.class);

            CreateRecTaskResponse resp = client.CreateRecTask(req);

            String result = SentenceRecognitionRequest.toJsonString(resp);
            JSONObject parseObject = JSONObject.parseObject(result);

//            System.out.println(SentenceRecognitionRequest.toJsonString(resp));
            return parseObject.getJSONObject("Data").getInteger("TaskId");
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * 请求识别结果
     * @param taskId
     * @return
     */
    public String result(Integer taskId) {
        try{
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(requestUrl);

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            AsrClient client = new AsrClient(cred, "ap-shanghai", clientProfile);

            String params = "{\"TaskId\":" + taskId + "}";
            DescribeTaskStatusRequest req = DescribeTaskStatusRequest.fromJsonString(params, DescribeTaskStatusRequest.class);

            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);

//            System.out.println(DescribeTaskStatusRequest.toJsonString(resp));
            return DescribeTaskStatusRequest.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }

}
