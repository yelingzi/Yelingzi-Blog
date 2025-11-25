package com.yeling.yelingziblog.chat.consumer;

import com.yeling.yelingziblog.chat.dto.PushMessageData;
import com.yeling.yelingziblog.chat.dto.PushMessageDto;
import com.yeling.yelingziblog.common.dto.NettyPushMessage;
import com.yeling.yelingziblog.websocket.NettyWebSocketServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageSendConsumer {



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "chat.single.queue"),
            exchange = @Exchange(name = "chat.exchange"),
            key = "chat.single"))
    public void messageSend(PushMessageDto dto) {
        log.info("发送消息 sender={}, receiver={}", dto.getSender(), dto.getReceiver());

        NettyPushMessage nettyPushMessageMe = new NettyPushMessage("success", "chat",
                new PushMessageData(dto.getReceiver(), "single",dto.getMessage()));
        NettyPushMessage nettyPushMessageToUser = new NettyPushMessage("success", "chat",
                new PushMessageData(dto.getSender(), "single", dto.getMessage()));
        // 1. 推给自己
        boolean selfOk = NettyWebSocketServerHandler.sendMessage(dto.getSender(), nettyPushMessageMe);
        // 2. 推给对方
        boolean recvOk = NettyWebSocketServerHandler.sendMessage(dto.getReceiver(), nettyPushMessageToUser);

        if (!recvOk) {
            log.info("用户 {} 不在线", dto.getReceiver());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "chat.group.queue"),
            exchange = @Exchange(name = "chat.exchange"),
            key = "chat.group"
    ))
    public void groupMessageSend(PushMessageDto pushMessageResp) {
        log.info("发送群聊消息：{}", pushMessageResp);
        NettyPushMessage nettyPushMessage = new NettyPushMessage("success", "chat",
                new PushMessageData(pushMessageResp.getReceiver(), "group", pushMessageResp.getMessage()));
        NettyWebSocketServerHandler.broadcastAllMessage(nettyPushMessage);
    }

}
