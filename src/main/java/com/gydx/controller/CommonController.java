package com.gydx.controller;

import com.alibaba.fastjson.JSONObject;
import com.gydx.dto.CommonRecognitionDTO;
import com.gydx.exception.BaiduAIException;
import com.gydx.response.Response;
import com.gydx.utils.BaiduAIUtil;
import com.gydx.utils.UploadToFtpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author 拽小白
 * @createTime 2020-11-06 22:04
 * @description 通用的接口类，用来提供一些基本的功能接口例如上传录音文件等
 */
@RestController
@RequestMapping("common")
@Api(tags = "通用接口")
public class CommonController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UploadToFtpUtil uploadToFtpUtil;
    @Resource
    private BaiduAIUtil baiduAIUtil;

    @PostMapping("upload")
    @ApiOperation(value = "上传文件至ftp服务器", httpMethod = "POST")
    public JSONObject upload(MultipartFile file) {
        String url;
        try {
            url = uploadToFtpUtil.upload(file.getInputStream());
        } catch (IOException ioException) {
            log.error("获取上传文件的输入流失败: {}", ioException.getMessage());
            return Response.fail("上传失败");
        }
        return Response.success("上传成功", url);
    }

    @PostMapping("recognition")
    @ApiOperation(value = "语音合成并上传到ftp服务器", httpMethod = "POST")
    public JSONObject recognition(@RequestBody CommonRecognitionDTO commonRecognitionDTO) {
        try {
            byte[] bytes = baiduAIUtil.speechSynthesis(commonRecognitionDTO.getText(), commonRecognitionDTO.getChoice());
            String url = uploadToFtpUtil.upload(new ByteArrayInputStream(bytes));
            return Response.success("语音合成成功", url);
        } catch (BaiduAIException exception) {
            log.error(exception.getMessage());
            return Response.fail(exception.getMessage());
        }
    }

}
