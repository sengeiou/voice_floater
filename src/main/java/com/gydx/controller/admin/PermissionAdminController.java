package com.gydx.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.gydx.entity.Permission;
import com.gydx.response.Response;
import com.gydx.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 14:27
 * @description
 */
@RestController
@RequestMapping("menu")
@Api(tags = "后台菜单接口")
@RequiresRoles("admin")
@Slf4j
public class PermissionAdminController {

    @Resource
    private PermissionService permissionService;

    @GetMapping("getAllMenu")
    @ApiOperation(value = "获取所有菜单", httpMethod = "GET")
    public JSONObject getAllMenu() {
        try {
            List<Permission> permissionList = permissionService.getAllMenu();
            return Response.success("获取成功", permissionList);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.fail("获取失败");
        }
    }

}
