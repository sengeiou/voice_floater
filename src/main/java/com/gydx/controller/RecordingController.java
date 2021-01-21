package com.gydx.controller;

import com.alibaba.fastjson.JSONObject;
import com.gydx.async.RecordingMethods;
import com.gydx.dto.FloaterAuditionDTO;
import com.gydx.dto.FloaterSalvageDTO;
import com.gydx.dto.FloaterSynthesisDTO;
import com.gydx.entity.Recording;
import com.gydx.exception.BaiduAIException;
import com.gydx.response.Response;
import com.gydx.service.RecordingService;
import com.gydx.utils.BaiduAIUtil;
import com.gydx.utils.RedisUtil;
import com.gydx.utils.TencentCloudUtil;
import com.gydx.utils.UploadToFtpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 拽小白
 */
@RestController
@RequestMapping("recording")
@Api(tags = "录音")
public class RecordingController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${recording.redisSetName}")
    private String redisSetName;

    @Resource
    private RecordingService recordingService;
    @Resource
    private BaiduAIUtil baiduAIUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UploadToFtpUtil uploadToFtpUtil;

    @GetMapping("findByUserId/{userId}")
    @ApiOperation(value = "获取该用户的所有漂流瓶", httpMethod = "GET")
    public JSONObject getAll(@PathVariable("userId") String userId) {
        try {
            List<Recording> recordings = recordingService.findByUserId(userId);
            return Response.success("查询成功", recordings);
        } catch (Exception e) {
            log.error("获取用户 {} 的所有漂流瓶出错，错误：{}", userId, e.getMessage());
            return Response.fail("查询失败");
        }
    }

    @PostMapping(value = "saveTypeOne/{userId}")
    @ApiOperation(value = "第一种方式的抛漂流瓶", httpMethod = "POST")
    public JSONObject saveTypeOne(@RequestParam("record") MultipartFile file, @PathVariable String userId) {
        if (file.isEmpty()) {
            return Response.fail("文件上传失败！");
        }
        try {
            Integer recordingId = recordingService.saveTypeOne(file.getInputStream(), userId);
            if (recordingId == null) {
                return Response.fail("抛漂流瓶失败");
            }
            return Response.success("抛漂流瓶成功");
        } catch (IOException e) {
            log.error(e.getMessage());
            return Response.fail("抛漂流瓶失败");
        }
    }

    @PostMapping("/audition")
    @ApiOperation(value = "返回用户试听语音合成出的音频", httpMethod = "POST")
    public JSONObject audition(@RequestBody FloaterAuditionDTO floaterAuditionDTO) {
        byte[] data;
        try {
            data = baiduAIUtil.speechSynthesis(floaterAuditionDTO.getText(), floaterAuditionDTO.getChoice());
            String fileUrl = uploadToFtpUtil.upload(new ByteArrayInputStream(data));
            return Response.success("语音合成成功", fileUrl);
        } catch (BaiduAIException e) {
            log.error("语音合成失败，{}", e.getMessage());
            return Response.fail("语音合成失败");
        }
    }

    @PostMapping("saveTypeTwo")
    @ApiOperation(value = "第二种方式的抛漂流瓶", httpMethod = "POST")
    public JSONObject saveTypeTwo(@RequestBody FloaterSynthesisDTO floaterSynthesisDTO) {
        try {
            System.out.println("floaterSynthesisDTO = " + floaterSynthesisDTO);
            Integer recordingId = recordingService.saveTypeTwo(floaterSynthesisDTO.getUrl(), floaterSynthesisDTO.getUserId());
            if (recordingId == null) {
                return Response.fail("漂流瓶保存失败");
            }
            return Response.success("抛漂流瓶成功");
        } catch (Exception e) {
            return Response.success("抛漂流瓶失败");
        }
    }

    @PostMapping("salvage")
    @ApiOperation(value = "用户打捞漂流瓶", httpMethod = "POST")
    public JSONObject salvage(@RequestBody FloaterSalvageDTO floaterSalvageDTO) {
        String recordingId;
        long size = redisUtil.sgetSetSize(redisSetName);
        if (size == 0) {
            // 如果当时没有漂流瓶，则返回打捞失败
            return Response.fail("打捞失败");
        }

        System.out.println("--------------------------------------");

        // 用户自己的所有漂流瓶id
        List<String> recordingIds = recordingService.findIdListByUserId(floaterSalvageDTO.getUserId());

        // 已经取出来但是不符合要求的
        List<String> contains = new ArrayList<>();
        // 如果取出来的id是该用户自己抛的，则再次取，直到取出来不是为止
        while (recordingIds.contains(recordingId = (String) redisUtil.sPop(redisSetName))) {
            System.out.println("recordingId = " + recordingId);
            contains.add(recordingId);
        }
        // 将取出来的不合要求的漂流瓶id再放入缓存中
        redisUtil.sset(redisSetName, contains);

        System.out.println("recordingId = " + recordingId);

        // 缓存里已经没有不是用户自己的漂流瓶了
        if (recordingId == null) {
            return Response.fail("打捞失败");
        }

        // 根据取出来的漂流瓶id查询到漂流瓶
        Recording recording = recordingService.findById(recordingId);
        new RecordingMethods().getRecognitionAndSend(recording.getTaskId(), floaterSalvageDTO.getUserId());

        System.out.println("recording = " + recording);
        return Response.success("打捞成功", recording);
    }


}
