package com.gydx.config;

import com.baidu.aip.speech.AipSpeech;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiduAIConfig {

    @Value("${ai.appId}")
    private String appId;
    @Value("${ai.appKey}")
    private String appKey;
    @Value("${ai.secretKey}")
    private String secretKey;

    @Bean
    public AipSpeech aipSpeech() {
        AipSpeech client = new AipSpeech(appId, appKey, secretKey);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;
    }

}
