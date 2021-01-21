package com.gydx.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 拽小白
 * @createTime 2020-11-29 11:48
 * @description
 */
@Data
@ApiModel(value = "userAdminLoginDTO", description = "用于后台登录的的用户DTO")
public class UserAdminLoginDTO {

    @ApiModelProperty(value = "用户名", example = "admin")
    private String nickName;
    @ApiModelProperty(value = "密码", example = "admin")
    private String password;

}
