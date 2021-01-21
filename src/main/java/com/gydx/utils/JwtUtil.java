package com.gydx.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gydx.entity.Role;
import com.gydx.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 拽小白
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public boolean verify(String token, String userName, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("userName", userName)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户id
     */
    public String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的角色信息
     * @param token
     * @return
     */
    public List<String> getRoles(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("roles").asList(String.class);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名
     * @return 加密的token
     */
    public String sign(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        List<String> roleList = new ArrayList<>();

        for (Role role : user.getRoles()) {
            roleList.add(role.getRoleName());
        }

        return JWT.create()
                .withClaim("openId", user.getOpenId())
                .withClaim("roleList", roleList)
                .withClaim("userId", user.getId())
                .withClaim("lastTime", user.getLastTime())
                .sign(algorithm);
    }

}
