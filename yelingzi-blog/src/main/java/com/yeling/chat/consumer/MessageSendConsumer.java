package com.yeling.chat.consumer;

import com.yeling.chat.domain.dto.PushMessageDto;
import com.yeling.websocket.NettyWebSocketServerHandler;
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

        // 1. 推给自己
        boolean selfOk = NettyWebSocketServerHandler.sendToUser(dto.getSender(), dto);
        // 2. 推给对方
        boolean recvOk = NettyWebSocketServerHandler.sendToUser(dto.getReceiver(), dto);

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
        NettyWebSocketServerHandler.broadcastAll(pushMessageResp);
    }

}
