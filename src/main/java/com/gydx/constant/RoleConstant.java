package com.gydx.constant;

/**
 * @author 拽小白
 * @createTime 2020-10-30 13:30
 * @description
 */
public enum RoleConstant {

    // 普通用户角色的id
    ORDINARY_USER(1, "普通用户"),
    // vip用户角色的id
    VIP_USER(2, "vip用户");

    private final Integer roleId;
    private final String roleName;

    RoleConstant(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

}
