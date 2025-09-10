package com.yeling.chat.controller;

import com.yeling.chat.domain.dto.NewMessageCountDto;
import com.yeling.chat.service.ChatService;
import com.yeling.chat.vo.request.ChatMessageReq;
import com.yeling.chat.vo.request.MessageListReq;
import com.yeling.chat.vo.request.SendMessageReq;
import com.yeling.chat.vo.response.MessageListResp;
import com.yeling.chat.vo.response.NewGroupChatBriefResp;
import com.yeling.chat.vo.response.NewSingleChatBriefResp;
import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.utils.IpUtils;
import com.yeling.utils.JwtUtils;
import com.yeling.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
public class ChatController {

    @Autowired private ChatService chatService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/chat/send")
    public ApiResponse sendSingleMessage(@RequestBody SendMessageReq sendMessageReq, HttpServletRequest request,
                                         @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                         @RequestHeader("x-host") String deviceId) {

        User user = new User();
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客" + deviceId);
        } else {
            user = JwtUtils.getUser(jwtToken);
        }

        chatService.sendMessage(user, sendMessageReq, IpUtils.getIpAddr(request));
        return ApiResponse.success();
    }

    @PostMapping("/api/chat/send/image")
    public ApiResponse sendImageMessage(@RequestParam("image") MultipartFile multipartFile, HttpServletRequest request,
                                        @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                        @RequestHeader("x-host") String deviceId) {

        User user = new User();
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客" + deviceId);
        } else {
            user = JwtUtils.getUser(jwtToken);
        }

        chatService.sendImageMessage(user, multipartFile, IpUtils.getIpAddr(request));

        return ApiResponse.success();
    }

    @PostMapping("/api/admin/chat/send/single")
    public ApiResponse adminSendSingleMessage(@RequestBody ChatMessageReq chatMessageReq,
                                              HttpServletRequest request) {

        User user = UserContext.getUser();
        chatService.adminSendSingleMessage(user, chatMessageReq.getMessage(), IpUtils.getIpAddr(request), chatMessageReq.getToUser());

        return ApiResponse.success();
    }


    /**
     * 获取聊天消息列表
     *
     * @param messageListReq 消息列表请求参数，包含游标、方向、类型、数量等信息
     * @param jwtToken 用户身份认证令牌
     * @param deviceId 设备ID
     * @return 返回消息列表结果，包含消息数据或错误信息
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/chat/list")
    public ApiResponse getMessageList(@Valid @RequestBody MessageListReq messageListReq,
                                      @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                      @RequestHeader("x-host") String deviceId) {

        User user = new User();
        // 解析用户身份信息，如果JWT令牌为空则视为游客访问
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客" + deviceId);
            log.info("游客：{}，加载聊天记录，游标：{}，方向：{}，类型：{}，数量：{}",  user.getNickname(), messageListReq.getCursor(), messageListReq.getDirection(), messageListReq.getType(), messageListReq.getLimit());
        } else {
            user = JwtUtils.getUser(jwtToken);
            log.info("用户：{}，加载聊天记录，游标：{}，方向：{}，类型：{}，数量：{}",  user.getId(), messageListReq.getCursor(), messageListReq.getDirection(), messageListReq.getType(), messageListReq.getLimit());
        }

        // 调用服务层获取消息列表
        MessageListResp messageListResp = chatService.getMessageList(messageListReq, user);
        if (messageListResp == null) {
            return ApiResponse.error("加载聊天记录失败");
        }
        return ApiResponse.success(messageListResp);
    }



    /**
     * 获取新的单聊消息列表
     *
     * @param cursor 游标，用于分页查询
     * @param jwtToken JWT认证令牌，用于用户身份验证
     * @param deviceId 设备ID，用于标识用户设备
     * @return 返回新的单聊消息列表结果
     */
    @GetMapping(value = "/api/chat/single/new")
    public ApiResponse getNewSingleChatCount(@Valid @RequestParam Integer cursor,
                                             @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                             @RequestHeader("x-host") String deviceId) {

        User user = new User();
        // 解析用户身份信息
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客" + deviceId);
            log.info("游客：{}，加载聊天新消息列表，游标：{}",  user.getNickname(), cursor);
        } else {
            user = JwtUtils.getUser(jwtToken);
            log.info("用户：{}，加载聊天新消息列表，游标：{}",  user.getId(), cursor);
        }


        // 查询新的单聊消息列表
        List<NewSingleChatBriefResp> newSingleChatBriefResp = chatService.getNewSingleChatCount(new NewMessageCountDto(cursor, user.getNickname(), user.getId()));
        if (newSingleChatBriefResp == null) {
            return ApiResponse.error("加载聊天列表失败");
        }
        return ApiResponse.success(newSingleChatBriefResp);
    }

    /**
     * 获取新的单聊消息列表
     *
     * @param cursor 游标，用于分页查询
     * @return 返回新的单聊消息列表结果
     */
    @GetMapping(value = "/api/chat/group/new")
    public ApiResponse getNewGroupChatCount(@Valid @RequestParam Integer cursor,
                                            @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                            @RequestHeader("x-host") String deviceId) {

        User user = new User();
        // 解析用户身份信息
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客" + deviceId);
            log.info("游客：{}，加载群聊新消息列表，游标：{}",  user.getNickname(), cursor);
        } else {
            user = JwtUtils.getUser(jwtToken);
            log.info("用户：{}，加载群聊新消息列表，游标：{}",  user.getId(), cursor);
        }


        // 查询新的单聊消息列表
        List<NewGroupChatBriefResp> newSingleChatBriefResp = chatService.getNewGroupChatCount(cursor);
        if (newSingleChatBriefResp == null) {
            return ApiResponse.error("加载聊天列表失败");
        }
        return ApiResponse.success(newSingleChatBriefResp);
    }


}
