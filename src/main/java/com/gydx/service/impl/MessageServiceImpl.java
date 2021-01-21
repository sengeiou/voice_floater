package com.gydx.service.impl;

import com.gydx.dto.MessageReplyDTO;
import com.gydx.entity.Message;
import com.gydx.entity.Recording;
import com.gydx.entity.User;
import com.gydx.mapper.MessageMapper;
import com.gydx.mapper.RecordingMapper;
import com.gydx.mapper.UserMapper;
import com.gydx.service.MessageService;
import com.gydx.utils.EmojiUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-12 12:10
 * @description
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RecordingMapper recordingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatch(List<Message> messages) {
        messageMapper.addBatch(messages);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message replyComplete(MessageReplyDTO messageReplyDTO) {
        User userFrom = userMapper.findById(messageReplyDTO.getUserFromId());
        userFrom.setNickName(EmojiUtil.htmlToEmoji(userFrom.getNickName()));
        User userTo = userMapper.findById(messageReplyDTO.getUserToId());
        userTo.setNickName(EmojiUtil.htmlToEmoji(userTo.getNickName()));
        Recording recording = recordingMapper.findById(messageReplyDTO.getRecordingId());
        Message message = new Message(messageReplyDTO);
        message.setUserFrom(userFrom);
        message.setUserTo(userTo);
        message.setRecording(recording);
        message.setQueue(messageReplyDTO.getQueue());
        return message;
    }

    @Override
    public List<Message> findByUserToId(String userToId) {
        return messageMapper.findBUserToId(userToId);
    }
}
