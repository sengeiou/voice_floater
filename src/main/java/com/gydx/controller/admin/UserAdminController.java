package com.gydx.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.gydx.dto.admin.UserAdminLoginDTO;
import com.gydx.entity.User;
import com.gydx.response.Response;
import com.gydx.service.admin.UserAdminService;
import com.gydx.utils.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-13 19:06
 * @description
 */
@RestController
@RequestMapping("userAdmin")
@Api(tags = "用户后台接口")
@Slf4j
@RequiresRoles("admin")
public class UserAdminController {

    @Resource
    private UserAdminService userAdminService;

    @PostMapping("login")
    @ApiOperation(value = "后台登录接口", httpMethod = "POST")
    public JSONObject login(@RequestBody UserAdminLoginDTO userAdminLoginDTO) {
        try {
            String token = userAdminService.login(userAdminLoginDTO);
            if (token == null) {
                return Response.fail("用户名或密码错误");
            }
            log.info("{} 于 {} 登录了", userAdminLoginDTO.getNickName(), CommonUtil.getNowTime());
            return Response.success("登录成功", token);
        } catch (Exception e) {
            return Response.fail("登录失败");
        }
    }

    @GetMapping("findAll")
    @ApiOperation(value = "查询所有用户", httpMethod = "GET")
    public JSONObject findAll() {
        try {
            List<User> userList = userAdminService.findAll();
            return Response.success("查询成功", userList);
        } catch (Exception e) {
            log.error("查询所有用户信息出错：{}", e.getMessage());
            return Response.fail("查询失败");
        }
    }



}
