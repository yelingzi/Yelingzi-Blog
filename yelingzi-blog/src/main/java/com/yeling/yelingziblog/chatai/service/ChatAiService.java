package com.yeling.yelingziblog.chatai.service;

import com.yeling.yelingziblog.chat.vo.request.ChatMessageReq;
import com.yeling.yelingziblog.chatai.vo.request.MoreChatReq;
import com.yeling.yelingziblog.user.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatAiService {

    Flux<String> chat(String userInput, String sessionId, User user, String ip);

    Flux<String> chats(MoreChatReq req, User user, String ip);

    Flux<String> appChat(String userInput, String sessionId, User user, String ip);

    Mono<Void> appChats(MoreChatReq req, User user, String ip);
}
