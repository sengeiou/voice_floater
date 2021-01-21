package com.gydx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Component
public class HttpUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String sendGet(String url) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (MalformedURLException me) {
            logger.error("url转换错误：{}", url);
        } catch (IOException ie) {
            logger.error("请求连接失败，错误：{}", ie.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return sb.toString();
    }

}
