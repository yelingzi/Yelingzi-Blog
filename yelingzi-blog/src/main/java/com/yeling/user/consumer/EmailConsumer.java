package com.yeling.user.consumer;

import com.rabbitmq.client.Channel;
import com.yeling.config.RabbitMQConfig;
import com.yeling.user.domian.dto.EmailMessage;
import com.yeling.user.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = RabbitMQConfig.EMAIL_QUEUE,
                            durable = "true",
                            arguments = @Argument(
                                    name = "x-dead-letter-exchange",
                                    value = RabbitMQConfig.EMAIL_QUEUE + ".dlx"
                            )
                    ),
                    exchange = @Exchange(
                            name = RabbitMQConfig.EMAIL_EXCHANGE,
                            type = ExchangeTypes.DIRECT
                    ),
                    key = RabbitMQConfig.EMAIL_ROUTING_KEY
            )
    )
    public void processEmailMessage(EmailMessage emailMessage, Channel channel, Message message) throws IOException {
        log.info("接收到邮件发送任务: {}", emailMessage.getTo());

        try {
            boolean success = emailService.sendEmail(emailMessage);

            if (success) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("邮件发送成功: {}", emailMessage.getTo());
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.warn("邮件发送失败，消息已重新入队: {}", emailMessage.getTo());
            }
        } catch (Exception e) {
            log.error("处理邮件发送任务时发生异常: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    // 死信队列处理
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = RabbitMQConfig.EMAIL_QUEUE + ".dlq"),
                    exchange = @Exchange(name = RabbitMQConfig.EMAIL_QUEUE + ".dlx"),
                    key = "#"
            )
    )
    public void processFailedEmailMessage(EmailMessage emailMessage) {
        log.error("邮件发送最终失败，进入死信队列: {}", emailMessage.getTo());
        redisTemplate.delete(emailMessage.getTo());
    }

}
