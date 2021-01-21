package com.gydx.controller;

import com.alibaba.fastjson.JSONObject;
import com.gydx.dto.UserLoginDTO;
import com.gydx.entity.User;
import com.gydx.exception.UserException;
import com.gydx.response.Response;
import com.gydx.service.UserService;
import com.gydx.utils.CommonUtil;
import com.gydx.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * @author 拽小白
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;

    @Resource
    private HttpUtil httpUtil;
    @Resource
    private UserService userService;

    @PostMapping("login")
    @ApiOperation(value = "/login", response = JSONObject.class, httpMethod = "POST")
    public JSONObject login(@RequestBody UserLoginDTO userLoginDTO) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + userLoginDTO.getCode() + "&grant_type=authorization_code";
        String res = httpUtil.sendGet(url);
        if (res == null || "".equals(res)) {
            log.error("腾讯出错");
            return Response.fail("登录失败！");
        } else {
            JSONObject wxRes = JSONObject.parseObject(res);
            String openId = wxRes.getString("openid");
            String sessionKey = wxRes.getString("session_key");
            if (openId == null || "".equals(openId) || sessionKey == null || "".equals(sessionKey)) {
                log.error("获取到的openId为空");
                return Response.fail("登录失败");
            } else {
                User user = new User(userLoginDTO.getNickName(), openId, sessionKey);
                user.setLastTime(new Timestamp(System.currentTimeMillis()));
                try {
                    String token = userService.login(user);
                    log.info("{} 于 {} 登录了", userLoginDTO.getNickName(), CommonUtil.getNowTime());
                    JSONObject data = new JSONObject();
//                    data.put("userId", user.getId());
                    data.put("token", token);
                    data.put("lastTime", user.getLastTime());
                    return Response.success("登录成功", data);
//                    return Response.success("登录成功", token);
                } catch (UserException ue) {
                    log.info("{} 于 {} 请求登录被拒绝了",userLoginDTO.getNickName(), CommonUtil.getNowTime());
                    return Response.fail(ue.getMessage());
                } catch (Exception e) {
                    log.error("登录出错：{}", e.getMessage());
                    return Response.fail("登录失败");
                }
            }
        }
    }

}
