package com.gydx.dto;

import lombok.Data;

/**
 * @author 拽小白
 * @createTime 2020-11-01 18:42
 * @description
 */
@Data
public class MessageReplyDTO {

    private String userFromId;
    private String userToId;
    private String recordingId;
    private String content;
    private String queue;

}
