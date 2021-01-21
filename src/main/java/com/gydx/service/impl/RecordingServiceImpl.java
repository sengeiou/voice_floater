package com.gydx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gydx.async.RecordingMethods;
import com.gydx.dto.FloaterSubmitDTO;
import com.gydx.entity.Recording;
import com.gydx.mapper.RecordingMapper;
import com.gydx.response.Response;
import com.gydx.service.RecordingService;
import com.gydx.utils.RedisUtil;
import com.gydx.utils.TencentCloudUtil;
import com.gydx.utils.UploadToFtpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * @author 拽小白
 */
@Service
public class RecordingServiceImpl implements RecordingService {

    @Value("${recording.redisSetName}")
    private String redisSetName;

    @Resource
    private RecordingMapper recordingMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TencentCloudUtil tencentCloudUtil;
    @Resource
    private UploadToFtpUtil uploadToFtpUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveTypeOne(InputStream fileInputStream, String userId) {

        String fileUrl = uploadToFtpUtil.upload(fileInputStream);
        if (!StringUtils.isEmpty(fileUrl)) {
            //                String convert = baiduAIUtil.convertToCharacters(file.getBytes());
            Recording recording = new Recording(fileUrl, userId, 1);
            recordingMapper.save(recording);
            // 将用户提交的漂流瓶id放入redis缓存中
            redisUtil.sset(redisSetName, String.valueOf(recording.getId()));
            return recording.getId();
        } else {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveTypeTwo(String url, String userId) {
        Recording recording = new Recording(url, userId, 2);
        System.out.println("recording = " + recording);
        recordingMapper.save(recording);
        System.out.println("recording = " + recording);
        // 将用户提交的漂流瓶id放入redis缓存中
        redisUtil.sset(redisSetName, String.valueOf(recording.getId()));
        return recording.getId();
    }

    @Override
    public void submit(FloaterSubmitDTO floaterSubmitDTO) {
        recordingMapper.submit(floaterSubmitDTO);
    }

    @Override
    public Recording findById(String recordingId) {
        Recording recording = recordingMapper.findById(recordingId);
        Integer taskId = tencentCloudUtil.recognition(recording.getUrl());
        recording.setTaskId(taskId);
        return recording;
    }

    /**
     * 获取该用户抛得所有漂流瓶的id列表
     */
    @Override
    public List<String> findIdListByUserId(String userId) {
        return recordingMapper.findIdListByUserId(userId);
    }

    @Override
    public List<Recording> findAll() {
        return recordingMapper.findAll();
    }

    @Override
    public List<Recording> findByUserId(String userId) {
        return recordingMapper.findByUserId(userId);
    }
}
