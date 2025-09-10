package com.yeling.chat.service.Impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yeling.chat.domain.dto.*;
import com.yeling.chat.domain.entity.Chat;
import com.yeling.chat.domain.entity.GroupChat;
import com.yeling.chat.mapper.ChatMapper;
import com.yeling.chat.service.ChatService;
import com.yeling.chat.vo.request.MessageListReq;
import com.yeling.chat.vo.request.SendMessageReq;
import com.yeling.chat.vo.response.*;
import com.yeling.exception.BaseException;
import com.yeling.user.domian.entity.User;
import com.yeling.common.service.Impl.ImageServiceImpl;
import com.yeling.user.vo.response.UserInfoResp;
import com.yeling.user.mapper.UserMapper;
import com.yeling.utils.HtmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ImageServiceImpl imageService;

    private static final String Admin_ID = "6";

    private static final String Admin_Nickname = "叶玲子";

    private static final String MessageTypeFile = "file";

    private static final String MessageTypeText = "text";

    private static final String MessageTypeImage = "image";

    private static final List<String> GROUP_MESSAGE_LIST = Arrays.asList(
            "大家好吖，喵！", "喵喵喵，喵！",
            "今天也是元气满满的一天呢~",
            "(≧∇≦)ﾉ",
            "awsl！这也太可爱了吧",
            "摸鱼ing，勿扰~",
            "qwq",
            "ovo"
    );

    private static final String SINGLE = "single";
    private static final String GROUP = "group";
    private static final String AFTER = "after";
    private static final String BEFORE = "before";

    @Override
    public void sendMessage(User user, SendMessageReq sendMessageReq, String ip) {

        String isUser = user.getId() == 0 ? "游客" : "用户";
        if(sendMessageReq.getType().equals(SINGLE)){
            log.info("{}：{}，发送单聊消息：{}",isUser, user.getNickname(), sendMessageReq.getMessage());
            sendSingleMessage(user, sendMessageReq.getMessage(), ip, MessageTypeText);
        } else if (sendMessageReq.getType().equals(GROUP)) {
            log.info("{}：{}，发送群聊消息：{}",isUser, user.getNickname(), sendMessageReq.getMessage());
            sendGroupMessage(user, sendMessageReq.getMessage(), ip);
        }

    }

    @Override
    public void sendImageMessage(User user, MultipartFile multipartFile, String ip){

        String isUser = user.getId() == 0 ? "游客" : "用户";

        String path = imageService.uploadImage(multipartFile, "/chat");

        log.info("{}：{}，发送图片消息：{}",isUser, user.getNickname(), path);

        sendSingleMessage(user, path, ip, MessageTypeImage);

    }

    @Override
    public ChatMessageResp adminSendSingleMessage(User user, String message, String ip, String toUser) {

        SingleChatMessageDto chatMessageDto = new SingleChatMessageDto(0, user.getId(), user.getNickname(), checkMessage(message), toUser,  user.getNickname(), ip, MessageTypeText);

        log.info("管理员发送消息, ID：{} ，接收对象：{}，消息内容： {}", user.getId(),  toUser, chatMessageDto.getMessage());

        int id = chatMapper.addChat(chatMessageDto);
        Chat chat = chatMapper.selectById(id);

        ChatMessageResp chatMessageResp = convertToChatMessageResp(chat);

        rabbitTemplate.convertAndSend("chat.exchange", "chat.single", new PushMessageDto(toUser, user.getId().toString(),chatMessageResp));

        return chatMessageResp;
    }

    /**
     * 获取消息列表
     *
     * @param req 消息列表请求参数，包含类型、游标、限制数量等信息
     * @param user 当前用户信息，用于获取用户ID和昵称
     * @return 消息列表响应对象，包含消息数据和分页信息
     */
    @Override
    public MessageListResp getMessageList(MessageListReq req, User user) {
        // 构造消息列表数据传输对象
        MessageListDto dto = new MessageListDto(
                user.getId(),
                user.getNickname(),
                Admin_ID,
                req.getCursor(),
                req.getLimit() + 1
        );

        // 根据消息类型获取对应的消息列表
        List<ChatMessageResp> data = switch (req.getType()) {
            case SINGLE -> getSingleMessages(dto, req.getDirection(), user.getId() == 0);
            case GROUP -> getGroupMessages(dto, req.getDirection());
            default -> Collections.emptyList();
        };

        // 构建并返回响应结果
        return buildResponse(data, req.getLimit());
    }

    /**
     * 获取用户的新单聊消息统计信息
     *
     * @param newMessageCountDto 包含用户ID和消息查询条件的数据传输对象
     * @return 返回按对方用户分组的新消息简要信息列表
     */
    @Override
    public List<NewSingleChatBriefResp> getNewSingleChatCount(NewMessageCountDto newMessageCountDto) {
        // 获取当前用户的所有新消息
        List<Chat> messages = getMessagesForUser(newMessageCountDto);

        // 按对方用户分组消息
        Map<String, List<Chat>> groupedMessages = groupMessagesByPartner(messages, newMessageCountDto);

        // 构建响应列表
        return buildChatBriefResponses(groupedMessages);
    }

    /**
     * 根据游标获取群聊消息的摘要列表，按会话分组并统计未读消息数量。
     *
     * @param cursor 游标，用于标识从哪个位置开始获取新消息
     * @return 群聊会话摘要列表，每个元素包含会话基本信息、未读消息数和最后一条消息内容
     */
    @Override
    public List<NewGroupChatBriefResp> getNewGroupChatCount(int cursor) {
        // 1. 获取游标之后的群聊消息
        List<GroupChat> messages = chatMapper.getNewGroupMessageListAfterCursor(cursor);

        // 2. 按会话ID(toGroup)分组聚合
        Map<String, List<GroupChat>> groupMap = messages.stream()
                .collect(Collectors.groupingBy(GroupChat::getToGroup));

        // 3. 构建响应列表
        List<NewGroupChatBriefResp> result = new ArrayList<>();
        for (Map.Entry<String, List<GroupChat>> entry : groupMap.entrySet()) {
            String groupId = entry.getKey();
            List<GroupChat> groupMessages = entry.getValue();

            // 获取最后一条消息（按时间倒序）
            GroupChat lastMsg = groupMessages.stream()
                    .max(Comparator.comparing(GroupChat::getCreateTime))
                    .orElse(null);

            if (lastMsg == null) continue;

            // 构建会话摘要响应
            NewGroupChatBriefResp resp = new NewGroupChatBriefResp();

            // 设置会话基本信息
            ChatInfoDto info = new ChatInfoDto();
            info.setId(groupId);
            info.setType("group");
            info.setNickname("聊天室：" + groupId);
            info.setAvatar("");

            resp.setInfo(info);

            // 设置未读消息数量
            resp.setCount(groupMessages.size());

            // 设置最后一条消息
            ChatMessageResp lastMessageResp = new ChatMessageResp();
            lastMessageResp.setId(lastMsg.getId());
            lastMessageResp.setUserId(lastMsg.getUserId());
            lastMessageResp.setNickname(lastMsg.getNickname());
            lastMessageResp.setUserAvatar(lastMsg.getUserAvatar());
            lastMessageResp.setMessageType(lastMsg.getMessageType());
            lastMessageResp.setMessage(lastMsg.getMessage());
            lastMessageResp.setCreateTime(lastMsg.getCreateTime());
            resp.setLastMessage(lastMessageResp);

            result.add(resp);
        }

        return result;
    }

    private String checkMessage(String message) {
        log.info("message = " + message);
        message = HtmlUtils.stripHtmlTags(message);

        return SensitiveWordHelper.replace(message);

    }

    private void sendSingleMessage(User user, String message, String ip, String messageType) {

        SingleChatMessageDto chatMessageDto = new SingleChatMessageDto(0, user.getId(), user.getNickname(), checkMessage(message), Admin_ID,  Admin_Nickname, ip, messageType);

        int id = chatMapper.addChat(chatMessageDto);

        Chat chat = chatMapper.selectById(chatMessageDto.getId());
        ChatMessageResp chatMessageResp = convertToChatMessageResp(chat);
        String sender = user.getId().toString();
        if(sender.equals("0")){
            sender = user.getNickname();
        }

        rabbitTemplate.convertAndSend("chat.exchange", "chat.single", new PushMessageDto(Admin_ID, sender, chatMessageResp));


    }

    private void sendGroupMessage(User user, String message, String ip) {
        // 检查消息是否合法
        String error = checkGroupMessage(message);
        if (error != null) {
            throw new BaseException(400, error);
        }

        GroupChatMessageDto chatMessageDto = new GroupChatMessageDto(0, user.getId(), user.getNickname(), message, "chatroom", ip, MessageTypeText);


        int id = chatMapper.addGroupChat(chatMessageDto);
        ChatMessageResp chatMessageResp = chatMapper.selectGroupChatById(chatMessageDto.getId());
        String sender = user.getId().toString();
        if(sender.equals("0")){
            sender = user.getNickname();
        }

        rabbitTemplate.convertAndSend("chat.exchange", "chat.group", new PushMessageDto("chatroom", sender, chatMessageResp));

    }

    /**
     * 检查消息是否在预定义列表中
     * @param message 用户发送的消息
     * @return 错误信息（如果消息不在列表中），否则返回null
     */
    private String checkGroupMessage(String message) {
        if (!GROUP_MESSAGE_LIST.contains(message)) {
            return "非法消息内容: 只允许发送预定义的消息";
        }
        return null;
    }

    private List<ChatMessageResp> getSingleMessages(MessageListDto dto, String direction, boolean useNickname) {
        return switch (direction) {
            case AFTER -> useNickname ?
                    chatMapper.getSingleMessageListAfterCursorByNicknameAndToChat(dto) :
                    chatMapper.getSingleMessageListAfterCursorByUserIdAndToChat(dto);
            case BEFORE -> useNickname ?
                    chatMapper.getSingleMessageListBeforeCursorByNicknameAndToChat(dto) :
                    chatMapper.getSingleMessageListBeforeCursorByUserIdAndToChat(dto);
            default -> Collections.emptyList();
        };
    }

    private List<ChatMessageResp> getGroupMessages(MessageListDto dto, String direction) {
        return switch (direction) {
            case AFTER -> chatMapper.getGroupMessageListAfterCursor(dto);
            case BEFORE -> chatMapper.getGroupMessageListBeforeCursor(dto);
            default -> Collections.emptyList();
        };
    }

    private MessageListResp buildResponse(List<ChatMessageResp> data, int limit) {
        MessageListResp resp = new MessageListResp();
        boolean hasMore = data.size() > limit;

        if (hasMore) {
            // 移除多余的一条记录（用于判断是否有更多数据）
            resp.setList(data.subList(0, limit));
        } else {
            resp.setList(data);
        }

        resp.setHasMore(hasMore);
        return resp;
    }

    private List<Chat> getMessagesForUser(NewMessageCountDto dto) {
        if (dto.getUserId() == 0) {
            return chatMapper.getSingleMessageListAfterCursorByNickname(dto.getNickname(), dto.getCursor());
        } else {
            return chatMapper.getSingleMessageListAfterCursorByUserId(dto.getUserId(), dto.getCursor());
        }
    }

    private Map<String, List<Chat>> groupMessagesByPartner(List<Chat> messages, NewMessageCountDto dto) {
        Map<String, List<Chat>> groupedMessages = new HashMap<>();

        for (Chat chat : messages) {
            // 确定对方用户标识
            String partnerIdentifier = determinePartnerIdentifier(chat, dto);

            // 按对方用户分组
            groupedMessages.computeIfAbsent(partnerIdentifier, k -> new ArrayList<>()).add(chat);
        }
        log.info("groupedMessages = ");
        groupedMessages.forEach((partnerIdentifier, chats) -> {
            log.info("partnerIdentifier = " + partnerIdentifier);
            chats.forEach(chat -> log.info("chat = " + chat));
        });

        return groupedMessages;
    }

    private String determinePartnerIdentifier(Chat chat, NewMessageCountDto dto) {
        // 如果当前用户是发送者，则对方是接收者
        if (isCurrentUserSender(chat, dto)) {
            return chat.getToUser();
        }
        // 如果当前用户是接收者，则对方是发送者
        else {
            return chat.getUserId() != 0 ? String.valueOf(chat.getUserId()) : chat.getNickname();
        }
    }

    private boolean isCurrentUserSender(Chat chat, NewMessageCountDto dto) {
        if (dto.getUserId() != 0) {
            return chat.getUserId() == dto.getUserId();
        } else {
            return chat.getNickname().equals(dto.getNickname());
        }
    }

    private ChatMessageResp convertToChatMessageResp(Chat chat) {
        ChatMessageResp resp = new ChatMessageResp();
        resp.setMessageType(chat.getMessageType());
        resp.setMessage(chat.getMessage());
        resp.setId(chat.getId());
        resp.setNickname(chat.getNickname());
        resp.setUserId(chat.getUserId());
        resp.setCreateTime(chat.getCreateTime());
        return resp;
    }

    private List<ChatMessageResp> buildChatMessageList(List<Chat> messages) {
        List<ChatMessageResp> responses = new ArrayList<>();
        for (Chat chat : messages) {
            responses.add(convertToChatMessageResp(chat));
        }
        return responses;
    }

    private List<NewSingleChatBriefResp> buildChatBriefResponses(Map<String, List<Chat>> groupedMessages) {
        List<NewSingleChatBriefResp> result = new ArrayList<>();

        for (Map.Entry<String, List<Chat>> entry : groupedMessages.entrySet()) {
            String partnerIdentifier = entry.getKey();
            List<Chat> messages = entry.getValue();

            if(messages.isEmpty()){
                continue;
            }

            // 构建会话信息
            ChatInfoDto chatInfo = buildChatInfo(partnerIdentifier, messages);

            // 构建响应对象
            NewSingleChatBriefResp briefResp = new NewSingleChatBriefResp();
            briefResp.setInfo(chatInfo);
            briefResp.setMessages(buildChatMessageList(messages));

            result.add(briefResp);
        }

        return result;
    }

    private ChatInfoDto buildChatInfo(String partnerIdentifier, List<Chat> messages) {
        ChatInfoDto chatInfo = new ChatInfoDto();
        chatInfo.setType("single");
        chatInfo.setId(partnerIdentifier);

        // 尝试解析对方用户ID
        try {
            int partnerUserId = Integer.parseInt(partnerIdentifier);
            // 对方是注册用户，通过ID查询用户信息
            UserInfoResp userInfoResp = userMapper.getInfo(partnerUserId);
            if (userInfoResp != null) {
                chatInfo.setNickname(userInfoResp.getNickname());
                chatInfo.setAvatar(userInfoResp.getUserAvatar());
            } else {
                chatInfo.setNickname("用户已注销");
                chatInfo.setAvatar("");
            }
        } catch (NumberFormatException e) {
            // 对方是访客用户，使用消息中的信息
            chatInfo.setNickname(partnerIdentifier);
            chatInfo.setAvatar("");
        }

        return chatInfo;
    }


}
