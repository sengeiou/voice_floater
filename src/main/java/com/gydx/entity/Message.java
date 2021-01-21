package com.gydx.entity;

import com.gydx.dto.ChatSendDTO;
import com.gydx.dto.MessageReplyDTO;
import com.gydx.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 拽小白
 * @createTime 2020-10-31 11:32
 * @description
 */
@Data
@NoArgsConstructor
public class Message implements Serializable {

    private Integer id;
    private String userFromId;
    private User userFrom;
    private String userToId;
    private User userTo;
    private String recordingId;
    private Recording recording;
    private String content;
    private String queue;
    private String sendTime;

    public Message(MessageReplyDTO messageReplyDTO) {
        this.setContent(messageReplyDTO.getContent());
        this.setUserToId(messageReplyDTO.getUserToId());
        this.setUserFromId(messageReplyDTO.getUserFromId());
        this.setRecordingId(messageReplyDTO.getRecordingId());
        this.setSendTime(CommonUtil.getNowTime());
        this.setQueue(messageReplyDTO.getQueue());
    }

    public Message(Integer id, String userFromId, String userToId, String recordingId, String content, String queue, String sendTime) {
        this.id = id;
        this.userFromId = userFromId;
        this.userToId = userToId;
        this.recordingId = recordingId;
        this.content = content;
        this.queue = queue;
        this.sendTime = sendTime;
    }

    public Message(ChatSendDTO chatSendDTO) {
        this.queue = chatSendDTO.getQueue();
        this.userToId = chatSendDTO.getUserToId();
        this.userFromId = chatSendDTO.getUserFromId();
        this.content = chatSendDTO.getContent();
        this.sendTime = CommonUtil.getNowTime();
    }
}
