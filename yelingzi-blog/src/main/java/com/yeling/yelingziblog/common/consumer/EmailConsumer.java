package com.yeling.yelingziblog.common.consumer;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.yeling.yelingziblog.common.config.RabbitMQConfig;
import com.yeling.yelingziblog.common.dto.EmailMessageDTO;
import com.yeling.yelingziblog.common.exception.EmailSendFailureException;
import com.yeling.yelingziblog.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String DLQ_KEY_PREFIX = "email:dlq:";

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = RabbitMQConfig.EMAIL_QUEUE + "v2",
                            durable = "true",
                            arguments = {
                                    @Argument(
                                            name = "x-dead-letter-exchange",
                                            value = RabbitMQConfig.EMAIL_QUEUE + ".dlx"
                                    ),
                                    @Argument(
                                            name = "x-dead-letter-routing-key",
                                            value = RabbitMQConfig.EMAIL_QUEUE + ".dlx"
                                    ),
                                    @Argument(
                                            name = "x-max-retries",
                                            value = "3"
                                    ),
                                    @Argument(
                                            name = "x-retry-delay",
                                            value = "5000"
                                    )
                            }
                    ),
                    exchange = @Exchange(
                            name = RabbitMQConfig.EMAIL_EXCHANGE,
                            type = ExchangeTypes.DIRECT
                    ),
                    key = RabbitMQConfig.EMAIL_ROUTING_KEY
            ),
            ackMode = "AUTO"  // 使用自动确认模式
    )
    public void processEmailMessage(EmailMessageDTO emailMessage) {
        String messageId = emailMessage.getMessageId();
        String maskedTo = emailService.maskEmail(emailMessage.getTo());

        log.info("开始处理邮件发送任务: messageId={}, to={}, retry={}",
                messageId, maskedTo, emailMessage.getRetryCount());

        try {
            boolean success = emailService.sendEmail(emailMessage);

            if (success) {
                log.info("邮件发送成功: messageId={}, to={}", messageId, maskedTo);
                // 自动确认消息
            } else {
                // 发送失败，抛出异常触发重试机制
                log.warn("邮件发送失败: messageId={}, to={}", messageId, maskedTo);
                throw new EmailSendFailureException();
            }
        } catch (Exception e) {
            log.error("处理邮件发送任务时发生异常: messageId={}, to={}, error={}",
                    messageId, maskedTo, e.getMessage(), e);

            // 检查是否达到最大重试次数
            if (emailMessage.canRetry()) {
                emailMessage.incrementRetry();
                log.warn("准备重试邮件发送: messageId={}, to={}, retry={}",
                        messageId, maskedTo, emailMessage.getRetryCount());
                // 抛出异常让RabbitMQ重新投递
                throw new AmqpRejectAndDontRequeueException("邮件发送失败，准备重试");
            } else {
                log.error("邮件发送达到最大重试次数，将进入死信队列: messageId={}, to={}",
                        messageId, maskedTo);
                // 达到最大重试次数，消息会自动进入死信队列
                // 这里可以记录日志或执行其他操作
                handleMaxRetriesReached(emailMessage);
            }
        }
    }

    /**
     * 处理达到最大重试次数的情况
     */
    private void handleMaxRetriesReached(EmailMessageDTO emailMessage) {
        try {
            // 预存储死信消息信息
            String key = DLQ_KEY_PREFIX + "pending:" + emailMessage.getMessageId();
            redisTemplate.opsForValue().set(key, emailMessage, Duration.ofHours(1));
            log.warn("邮件已达到最大重试次数，等待进入死信队列: messageId={}, to={}",
                    emailMessage.getMessageId(), emailService.maskEmail(emailMessage.getTo()));
        } catch (Exception e) {
            log.error("预存储死信消息失败: messageId={}, error={}",
                    emailMessage.getMessageId(), e.getMessage(), e);
        }
    }

    /**
     * 死信队列处理
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = RabbitMQConfig.EMAIL_QUEUE + ".dlq"),
                    exchange = @Exchange(name = RabbitMQConfig.EMAIL_QUEUE + ".dlx"),
                    key = "#"
            ),
            ackMode = "AUTO"  // 死信队列也使用自动确认
    )
    public void processFailedEmailMessage(EmailMessageDTO emailMessage) {
        log.error("邮件进入死信队列: messageId={}, to={}, retryCount={}",
                emailMessage.getMessageId(), emailService.maskEmail(emailMessage.getTo()),
                emailMessage.getRetryCount());

        try {
            // 存储死信消息到Redis，便于后续分析
            String key = DLQ_KEY_PREFIX + emailMessage.getMessageId();
            redisTemplate.opsForValue().set(key, emailMessage, Duration.ofDays(7));

            // 清理预存储的记录
            String pendingKey = DLQ_KEY_PREFIX + "pending:" + emailMessage.getMessageId();
            redisTemplate.delete(pendingKey);

            // TODO: 可以添加告警通知、记录到数据库等操作
            sendDlqNotification(emailMessage);

        } catch (Exception e) {
            log.error("处理死信队列消息失败: messageId={}, error={}",
                    emailMessage.getMessageId(), e.getMessage(), e);
        }
    }

    /**
     * 发送死信队列通知
     */
    private void sendDlqNotification(EmailMessageDTO emailMessage) {
        // TODO: 实现告警通知逻辑，如发送邮件、短信、钉钉通知等
        log.warn("死信队列告警: 邮件发送失败 messageId={}, to={}, 重试次数={}",
                emailMessage.getMessageId(),
                emailService.maskEmail(emailMessage.getTo()),
                emailMessage.getRetryCount());
    }
}

