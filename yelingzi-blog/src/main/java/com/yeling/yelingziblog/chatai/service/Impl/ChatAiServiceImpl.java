package com.yeling.yelingziblog.chatai.service.Impl;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.yeling.yelingziblog.chat.vo.request.ChatMessageReq;
import com.yeling.yelingziblog.chatai.dto.History;
import com.yeling.yelingziblog.chatai.entity.ChatAi;
import com.yeling.yelingziblog.chatai.entity.ChatAiUserLink;
import com.yeling.yelingziblog.chatai.service.ChatAiService;
import com.yeling.yelingziblog.chatai.service.HistoryService;
import com.yeling.yelingziblog.chatai.service.ToolService;
import com.yeling.yelingziblog.chatai.vo.request.MoreChatReq;
import com.yeling.yelingziblog.common.dto.NettyPushMessage;
import com.yeling.yelingziblog.common.exception.BaseException;
import com.yeling.yelingziblog.common.exception.TooManyAttemptsException;
import com.yeling.yelingziblog.common.service.RedisService;
import com.yeling.yelingziblog.common.utils.JwtUtils;
import com.yeling.yelingziblog.common.utils.WsPushUtil;
import com.yeling.yelingziblog.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatAiServiceImpl implements ChatAiService {

    private final HistoryService historyService;
    private final ToolService toolService;
    private final ChatClient chatClient;

    HashMap<String, ChatModel> platforms = new HashMap<>();

    private final static Map<String, Set<String>> PLATFORM_MAP = new HashMap<>();

    static {
        PLATFORM_MAP.put("dashscope", Set.of("qwen-plus-2025-04-28", "deepseek-r1-0528"));
    }

    private static final String CHAT_AI_ATTEMPTS_KEY_PREFIX = "chat_ai_attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 10;
    private static final long BLOCK_DURATION_SECONDS = TimeUnit.MINUTES.toSeconds(BLOCK_DURATION_MINUTES);

    private final RedisService redisService;

    private final RabbitTemplate rabbitTemplate;

    public ChatAiServiceImpl(@Qualifier("chatClient") ChatClient chatClient,
                             HistoryService historyService,
                             ToolService toolService,
                             RabbitTemplate rabbitTemplate,
                             DashScopeChatModel dashScopeChatModel,
                             RedisService redisService) {
        this.chatClient = chatClient;
        this.historyService = historyService;
        this.toolService = toolService;
        this.rabbitTemplate = rabbitTemplate;
        this.redisService = redisService;
        platforms.put("dashscope", dashScopeChatModel);
    }


    @Override
    public Flux<String> chat(String userInput, String sessionId, User user, String ip) {

        // 检查登录尝试次数
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }


        // 1. 查找用户是否存在会话 sessionId
        long count = historyService.getHistoryLinkCount(
                user.getId() == 0 ? user.getNickname() : user.getId() + "", sessionId);
        if(count == 0){
            throw new BaseException(400, "非法会话:" + sessionId);
        }

        // 2. 一次性查出历史，拼成 Message
        List<Message> historyMessages = historyService.getHistory(sessionId)
                .stream()
                .map(h -> "user".equals(h.getRole())
                        ? new UserMessage(h.getContent())
                        : new AssistantMessage(h.getContent()))
                .collect(Collectors.toList());

        // 3. 当前用户消息（也追加到历史表，方便后面查询）
        ChatAi userMessage = new ChatAi(0, sessionId, "user", userInput, null);
        saveHistory(userMessage);

        // 4. 线程安全地收集 AI 回复
        StringBuilder aiReply = new StringBuilder();

        return chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .system(spec -> {
                    spec.param("nickname", user.getNickname());
                    spec.param("user", user);
                })
                .messages(historyMessages)
                .user(userInput)
                .tools(toolService)
                .stream()
                .content()
                .doOnNext(aiReply::append)
                .doOnComplete(() -> {
                    // 5. 流正常结束再存 AI 回复
                    ChatAi aiMessage = new ChatAi(0, sessionId, "assistant", aiReply.toString(), null);
                    saveHistory(aiMessage);
                })
                .doOnError(err -> {
                    log.error("chat stream error, session={}",sessionId, err);
                    ChatAi aiMessage = new ChatAi(0, sessionId, "assistant", "服务异常", null);
                    saveHistory(aiMessage);
                });
    }


    @Override
    public Flux<String> chats(MoreChatReq  req, User user, String ip) {

        // 检查登录尝试次数
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }


        // 0. 参数是否合法
        String platform = req.getOptions().getPlatform();
        String model = req.getOptions().getModel();

        // 1. 校验平台是否存在
        if (platform == null || !PLATFORM_MAP.containsKey(platform)) {
            throw new IllegalArgumentException("非法平台: " + platform);
        }

        // 2. 校验模型是否属于该平台
        Set<String> models = PLATFORM_MAP.get(platform);
        if (model == null || !models.contains(model)) {
            throw new IllegalArgumentException("平台 " + platform + " 不支持模型: " + model);
        }

        // 1. 查找用户是否存在会话 sessionId
        long count = historyService.getHistoryLinkCount(
                user.getId() == 0 ? user.getNickname() : user.getId() + "", req.getSessionId());
        if(count == 0){
            throw new BaseException(400, "非法会话:" + req.getSessionId());
        }

        // 2. 一次性查出历史，拼成 Message
        List<Message> historyMessages = historyService.getHistory(req.getSessionId())
                .stream()
                .map(h -> "user".equals(h.getRole())
                        ? new UserMessage(h.getContent())
                        : new AssistantMessage(h.getContent()))
                .collect(Collectors.toList());

        // 3. 当前用户消息（也追加到历史表，方便后面查询）
        ChatAi userMessage = new ChatAi(0, req.getSessionId(), "user", req.getPrompt(), null);
        saveHistory(userMessage);

        // 4. 线程安全地收集 AI 回复
        StringBuilder aiReply = new StringBuilder();

        ChatModel chatModel = platforms.get(platform);

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient chatClient = builder.defaultOptions(
                ChatOptions.builder()
                        .temperature(req.getOptions().getTemperature())
                        .model(req.getOptions().getModel())
                        .build()
        ).build();

        return chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .messages(historyMessages)
                .user(req.getPrompt())
                .stream()
                .content()
                .doOnNext(aiReply::append)
                .doOnComplete(() -> {
                    // 5. 流正常结束再存 AI 回复
                    ChatAi aiMessage = new ChatAi(0, req.getSessionId(), "assistant", aiReply.toString(), null);
                    saveHistory(aiMessage);
                })
                .doOnError(err -> {
                    log.error("chat stream error, session={}", req.getSessionId(), err);
                    ChatAi aiMessage = new ChatAi(0, req.getSessionId(), "assistant", "服务异常", null);
                    saveHistory(aiMessage);
                });

    }


    @Override
    public Flux<String> appChat(String userInput,
                              String sessionId,
                              User user,
                              String ip) {

        // 1. 登录次数限流
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }

        // 2. 会话合法性校验
        long cnt = historyService.getHistoryLinkCount(
                user.getId() == 0 ? user.getNickname() : String.valueOf(user.getId()),
                sessionId);
        if (cnt == 0) {
            throw new BaseException(400, "非法会话:" + sessionId);
        }

        // 3. 历史消息一次性加载
        List<Message> historyMessages = historyService.getHistory(sessionId)
                .stream()
                .map(h -> "user".equals(h.getRole())
                        ? new UserMessage(h.getContent())
                        : new AssistantMessage(h.getContent()))
                .collect(Collectors.toList());

        // 4. 保存用户问题
        ChatAi userMsg = new ChatAi(0, sessionId, "user", userInput, null);
        saveHistory(userMsg);

        // 5. 构造推送对象
        String uid = user.getId() == 0 ? user.getNickname() : String.valueOf(user.getId());
        NettyPushMessage pushMsg = new NettyPushMessage();
        pushMsg.setMessageType("aichat");   // 前端根据该字段区分业务
        pushMsg.setData("");              // 先置空，后面每段追加

        StringBuilder aiReply = new StringBuilder();

        chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .system(spec -> spec.param("nickname", user.getNickname())
                        .param("user", user))
                .messages(historyMessages)
                .user(userInput)
                .tools(toolService)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    aiReply.append(chunk);
                    log.info("aiReply: {}", aiReply);
                    pushMsg.setData(chunk);   // 当前已拼接的完整回答
                    WsPushUtil.push(uid, pushMsg);         // 实时推到 WebSocket
                })
                .doOnComplete(() -> {
                    // 6. 全部收完再落库
                    ChatAi aiMsg = new ChatAi(0, sessionId, "assistant", aiReply.toString(), null);
                    saveHistory(aiMsg);
                    pushMsg.setData("DONE!!");
                    WsPushUtil.push(uid, pushMsg);
                })
                .doOnError(err -> {
                    log.error("chat stream error, session={}", sessionId, err);
                    pushMsg.setData("服务异常");
                    WsPushUtil.push(uid, pushMsg);
                    ChatAi aiMsg = new ChatAi(0, sessionId, "assistant", "服务异常", null);
                    saveHistory(aiMsg);
                    pushMsg.setData("DONE!!");
                    WsPushUtil.push(uid, pushMsg);
                })
                .subscribe();

        return Flux.empty();

    }

    @Override
    public Mono<Void> appChats(MoreChatReq  req, User user, String ip) {

        // 检查登录尝试次数
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }


        // 0. 参数是否合法
        String platform = req.getOptions().getPlatform();
        String model = req.getOptions().getModel();

        // 1. 校验平台是否存在
        if (platform == null || !PLATFORM_MAP.containsKey(platform)) {
            throw new IllegalArgumentException("非法平台: " + platform);
        }

        // 2. 校验模型是否属于该平台
        Set<String> models = PLATFORM_MAP.get(platform);
        if (model == null || !models.contains(model)) {
            throw new IllegalArgumentException("平台 " + platform + " 不支持模型: " + model);
        }

        // 1. 查找用户是否存在会话 sessionId
        long count = historyService.getHistoryLinkCount(
                user.getId() == 0 ? user.getNickname() : user.getId() + "", req.getSessionId());
        if(count == 0){
            throw new BaseException(400, "非法会话:" + req.getSessionId());
        }

        // 2. 一次性查出历史，拼成 Message
        List<Message> historyMessages = historyService.getHistory(req.getSessionId())
                .stream()
                .map(h -> "user".equals(h.getRole())
                        ? new UserMessage(h.getContent())
                        : new AssistantMessage(h.getContent()))
                .collect(Collectors.toList());

        // 3. 当前用户消息（也追加到历史表，方便后面查询）
        ChatAi userMessage = new ChatAi(0, req.getSessionId(), "user", req.getPrompt(), null);
        saveHistory(userMessage);

        // 5. 构造推送对象
        String uid = user.getId() == 0 ? user.getNickname() : String.valueOf(user.getId());
        NettyPushMessage pushMsg = new NettyPushMessage();
        pushMsg.setMessageType("aichat");   // 前端根据该字段区分业务
        pushMsg.setData("");              // 先置空，后面每段追加

        // 4. 线程安全地收集 AI 回复
        StringBuilder aiReply = new StringBuilder();

        ChatModel chatModel = platforms.get(platform);

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient chatClient = builder.defaultOptions(
                ChatOptions.builder()
                        .temperature(req.getOptions().getTemperature())
                        .model(req.getOptions().getModel())
                        .build()
        ).build();

       chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .messages(historyMessages)
                .user(req.getPrompt())
                .stream()
                .content()
                .doOnNext(chunk -> {
                    aiReply.append(chunk);
                    pushMsg.setData(chunk);   // 当前已拼接的完整回答
                    WsPushUtil.push(uid, pushMsg);         // 实时推到 WebSocket
                })
                .doOnComplete(() -> {
                    // 5. 流正常结束再存 AI 回复
                    ChatAi aiMessage = new ChatAi(0, req.getSessionId(), "assistant", aiReply.toString(), null);
                    saveHistory(aiMessage);
                    pushMsg.setData("DONE!!");
                    WsPushUtil.push(uid, pushMsg);
                })
                .doOnError(err -> {
                    log.error("chat stream error, session={}", req.getSessionId(), err);
                    ChatAi aiMessage = new ChatAi(0, req.getSessionId(), "assistant", "服务异常", null);
                    saveHistory(aiMessage);
                    pushMsg.setData("DONE!!");
                    WsPushUtil.push(uid, pushMsg);
                })
               .subscribe();

        return Mono.empty();
    }

    private void saveHistory(ChatAi chatAi) {
        rabbitTemplate.convertAndSend(
                "ai.exchange",
                "history.save",
                chatAi
        );
    }


    public boolean allowLoginAttempt(String ip) {
        String key = CHAT_AI_ATTEMPTS_KEY_PREFIX + ip;

        // 获取当前尝试次数
        Integer attempts = (Integer) redisService.get(key);

        // 如果已超过最大尝试次数，拒绝登录
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            return false;
        }

        // 增加尝试次数
        long newAttempts = redisService.increment(key, 1);

        // 如果是第一次尝试，设置过期时间
        if (newAttempts == 1) {
            redisService.expire(key, BLOCK_DURATION_SECONDS, TimeUnit.SECONDS);
        }

        return true;
    }


}
