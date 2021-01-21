package com.gydx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 拽小白
 * @createTime 2020-11-11 20:50
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Queue implements Serializable {

    private Integer id;
    private String queueName;
    private String userFromId;
    private User userFrom;
    private String userToId;
    private User userTo;

}
