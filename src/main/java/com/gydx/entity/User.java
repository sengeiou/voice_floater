package com.gydx.entity;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 拽小白
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Integer id;
    private String nickName;
    private String password;
    private String openId;
    private String sessionKey;
    private Timestamp lastTime;
    private int status;
    private String prohibitTime;
    private int prohibitDays;
    private List<Role> roles;

    public User(String nickName, String openId, String sessionKey) {
        this.nickName = nickName;
        this.openId = openId;
        this.sessionKey = sessionKey;
    }
}
