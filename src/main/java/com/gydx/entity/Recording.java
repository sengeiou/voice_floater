package com.gydx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 拽小白
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recording implements Serializable {

    private Integer id;
    private String userId;
    private User user;
    private String url;
    private Integer type;
    private Integer status;
    private Integer taskId;

    public Recording(String url, String userId, Integer type) {
        this.url = url;
        this.userId = userId;
        this.type = type;
    }

}
