package com.yeling.yelingziblog.other.service.Impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yeling.yelingziblog.other.entity.Message;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.other.vo.request.MessageReq;
import com.yeling.yelingziblog.other.vo.response.MessageResp;
import com.yeling.yelingziblog.other.mapper.MessageMapper;
import com.yeling.yelingziblog.other.service.MessageService;
import com.yeling.yelingziblog.common.utils.HtmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;


    @Override
    public void addMessage(String messageReq, User user){
        String content = HtmlUtils.stripHtmlTags(messageReq);
        content = SensitiveWordHelper.replace(content);
        messageMapper.addMessage(content,
                user.getId(), user.getNickname(), user.getUserAvatar());
    }

    @Override
    public void delMessage(Long id){
        messageMapper.updateMessageStateById(1, id);
    }

    @Override
    public void updateMessage(Long id){
        messageMapper.updateMessageStateById(2, id);
    }

    @Override
    public PageResult<Message> getMessageListByPage(int page, int pageSize){
        PageResult<Message> messagePageResult = new PageResult<>();
        messagePageResult.setPage(page);
        messagePageResult.setPageSize(pageSize);
        messagePageResult.setData(messageMapper.findMessageListByPage((page - 1) * pageSize, pageSize));
        messagePageResult.setTotal(messageMapper.findMessageCount());
        return messagePageResult;
    }

    @Override
    public PageResult<Message> userGetMessageListByPage(int page, int pageSize, Integer userId){
        PageResult<Message> messagePageResult = new PageResult<>();
        messagePageResult.setPage(page);
        messagePageResult.setPageSize(pageSize);
        messagePageResult.setData(messageMapper.findMessagePageListByUserId(userId, (page - 1) * pageSize, pageSize));
        messagePageResult.setTotal(messageMapper.findMessageCountByUserId(userId));
        return messagePageResult;
    }
    @Override
    @Cacheable(value = "message:list")
    public List<MessageResp> getMessageList(){
        return messageMapper.findMessageList();
    }

}
