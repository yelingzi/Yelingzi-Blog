package com.yeling.yelingziblog.chatai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.yeling.yelingziblog.chatai.service.ChatAiService;
import com.yeling.yelingziblog.chatai.vo.request.AppAiChatReq;
import com.yeling.yelingziblog.chatai.vo.request.MoreChatReq;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import com.yeling.yelingziblog.common.utils.IpUtils;
import com.yeling.yelingziblog.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class ChatAiController {

    private final ChatAiService chatAiService;


    public ChatAiController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generation(@RequestParam("prompt") String userInput, @RequestParam(value = "sessionId", required = false) String sessionId,
                                   @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                   @RequestHeader("x-host") String deviceId,
                                   HttpServletRequest request) {

        String ip = IpUtils.getIpAddr(request);
        return chatAiService.chat(userInput, sessionId, JwtUtils.getUserByTokenOrDeviceId(jwtToken, deviceId), ip);
    }

    @PostMapping("/chats")
    public Flux<String> chat (@RequestBody MoreChatReq req,
                              @RequestHeader(value = "Authorization", required = false) String jwtToken,
                              @RequestHeader("x-host") String deviceId,
                              HttpServletRequest request){
        String ip = IpUtils.getIpAddr(request);
        return chatAiService.chats(req, JwtUtils.getUserByTokenOrDeviceId(jwtToken, deviceId), ip);
    }

    @PostMapping(value = "/app/chat")
    public ApiResponse appAiChat(@RequestBody AppAiChatReq appAiChatReq,
                                   @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                   @RequestHeader("x-host") String deviceId,
                                   HttpServletRequest request) {

        String ip = IpUtils.getIpAddr(request);
        Flux<String> flux = chatAiService.appChat(appAiChatReq.getPrompt(), appAiChatReq.getSessionId(), JwtUtils.getUserByTokenOrDeviceId(jwtToken, deviceId), ip);
        return ApiResponse.success();
    }

    @PostMapping("/app/chats")
    public ApiResponse appAiChats (@RequestBody MoreChatReq req,
                                   @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                   @RequestHeader("x-host") String deviceId,
                                   HttpServletRequest request){
        String ip = IpUtils.getIpAddr(request);
        chatAiService.appChats(req, JwtUtils.getUserByTokenOrDeviceId(jwtToken, deviceId), ip);
        return ApiResponse.success();
    }

}
