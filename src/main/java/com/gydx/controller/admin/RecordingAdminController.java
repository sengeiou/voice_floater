package com.gydx.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.gydx.entity.Recording;
import com.gydx.response.Response;
import com.gydx.service.RecordingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 10:40
 * @description
 */
@RestController
@RequestMapping("recordingAdmin")
@Api(tags = "漂流瓶后台接口")
@Slf4j
public class RecordingAdminController {

    @Resource
    private RecordingService recordingService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询所有的漂流瓶", httpMethod = "GET")
    public JSONObject findAll() {
        try {
            List<Recording> recordingList = recordingService.findAll();
            return Response.success("查询成功", recordingList);
        } catch (Exception e) {
            log.error("查询所有的漂流瓶出错：{}", e.getMessage());
            return Response.fail("查询出错");
        }
    }

}
