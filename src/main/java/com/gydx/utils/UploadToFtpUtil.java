package com.gydx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author 拽小白
 */
@Component
public class UploadToFtpUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy/MM/dd"));

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private Integer port;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.baseUrl}")
    private String baseUrl;

    public String upload(InputStream input) {
        String filePath = SIMPLE_DATE_FORMAT_THREAD_LOCAL.get().format(new Date()) + "/";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".wav";
        FTPClient ftpClient = new FTPClient();
        try {
            int reply;
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.out.println("无回复");
                return null;
            }

            if (!ftpClient.changeWorkingDirectory(filePath)) {
                String[] dirs = filePath.split("/");
                String tempPath = "";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    tempPath += "/" + dir;
                    if (!ftpClient.changeWorkingDirectory(tempPath)) {
                        if (!ftpClient.makeDirectory(tempPath)) {
                            System.out.println("创建目录失败");
                            return null;
                        } else {
                            ftpClient.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if(!ftpClient.storeFile(fileName, input)) {
                return null;
            }
            input.close();
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return baseUrl + filePath + fileName;
    }

}
