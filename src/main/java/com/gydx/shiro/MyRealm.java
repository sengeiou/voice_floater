package com.gydx.shiro;

import com.gydx.entity.Permission;
import com.gydx.entity.User;
import com.gydx.jwt.JwtToken;
import com.gydx.service.RolePermissionService;
import com.gydx.service.UserService;
import com.gydx.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
public class MyRealm extends AuthorizingRealm {

    @Value("${jwt.secret}")
    private String secret;

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private UserService userService;
    @Resource
    private RolePermissionService rolePermissionService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println(principalCollection.toString());

        String userId = jwtUtil.getUserId(principalCollection.toString());
        User user = userService.findById(userId);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        user.getRoles().forEach(role -> {
            simpleAuthorizationInfo.addRole(role.getRoleName());
            List<Permission> permissions = rolePermissionService.findPermissionByRoleId(role.getId());
            permissions.forEach(permission -> {
                simpleAuthorizationInfo.addStringPermission(permission.getPermissionName());
            });
        });

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        String userId = jwtUtil.getUserId(token);
        if (StringUtils.isEmpty(userId)) {
            throw new AuthenticationException("token invalid");
        }

        User user = userService.findById(userId);
        if (user == null) {
            throw new AuthenticationException("User is not existed!");
        }

        if (!jwtUtil.verify(token, userId, secret)) {
            throw new AuthenticationException("用户名或密码错误");
        }

        return new SimpleAuthenticationInfo(token, token, getName());
    }

}
