package com.yeling.other.service;

import com.yeling.other.domian.entity.Message;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.MessageReq;
import com.yeling.other.vo.response.MessageResp;

import java.util.List;

public interface MessageService {

    void addMessage(MessageReq messageReq, User user);

    void delMessage(Long id);

    void updateMessage(Long id);

    PageResult<Message> getMessageListByPage(int page, int pageSize);

    PageResult<Message> userGetMessageListByPage(int page, int pageSize, Integer userId);

    List<MessageResp> getMessageList();

}
