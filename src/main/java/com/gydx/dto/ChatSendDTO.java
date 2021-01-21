package com.gydx.dto;

import lombok.Data;

/**
 * @author 拽小白
 * @createTime 2020-11-16 20:03
 * @description
 */
@Data
public class ChatSendDTO {

    private String userFromId;
    private String userToId;
    private String content;
    private String queue;

}
