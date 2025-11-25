package com.yeling.yelingziblog.chat.service;

import com.yeling.yelingziblog.chat.dto.NewMessageCountDto;
import com.yeling.yelingziblog.chat.vo.request.MessageListReq;
import com.yeling.yelingziblog.chat.vo.request.SendMessageReq;
import com.yeling.yelingziblog.chat.vo.response.ChatMessageResp;
import com.yeling.yelingziblog.chat.vo.response.MessageListResp;
import com.yeling.yelingziblog.chat.vo.response.NewGroupChatBriefResp;
import com.yeling.yelingziblog.chat.vo.response.NewSingleChatBriefResp;
import com.yeling.yelingziblog.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {

    void sendMessage(User user, SendMessageReq sendMessageReq, String ip);

    void sendEmojiMessage(User user, SendMessageReq sendMessageReq, String ip);


    void sendImageMessage(User user, MultipartFile multipartFile, String ip);

    void adminSendSingleMessage(User user, String message, String ip, String toUser, String type);
    void adminSendImageMessage(User user, MultipartFile message, String ip, String toUser);
    void adminSendEmojiMessage(User user, String message, String ip, String toUser, String type);

    MessageListResp getMessageList(MessageListReq messageListReq, User user);

    List<NewSingleChatBriefResp> getNewSingleChatCount(NewMessageCountDto newMessageCountDto);

    List<NewGroupChatBriefResp> getNewGroupChatCount(int cursor);

}
