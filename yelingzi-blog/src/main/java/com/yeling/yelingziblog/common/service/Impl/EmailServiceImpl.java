package com.yeling.yelingziblog.common.service.Impl;

import com.yeling.yelingziblog.common.config.EmailProperties;
import com.yeling.yelingziblog.common.config.RabbitMQConfig;
import com.yeling.yelingziblog.common.dto.EmailMessageDTO;
import com.yeling.yelingziblog.common.exception.BaseException;
import com.yeling.yelingziblog.common.exception.EmailSendFailureException;
import com.yeling.yelingziblog.common.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final AmqpTemplate rabbitTemplate;
    private final SpringTemplateEngine templateEngine;
    private final EmailProperties emailProperties;


    /**
     * 发送简单邮件（异步）
     */
    @Override
    public void sendEmailAsync(EmailMessageDTO message) {
        prepareMessage(message);
        sendToQueue(message);
    }

    /**
     * 发送模板邮件（异步）
     */
    @Override
    public void sendTemplateEmailAsync(String toEmail, String subject, String templateName,
                                       Map<String, Object> templateVariables, String businessType) {

        String content = renderTemplate(templateName, templateVariables);

        EmailMessageDTO message = EmailMessageDTO.builder()
                .to(toEmail)
                .subject(subject)
                .content(content)
                .businessType(businessType)
                .businessData(templateVariables != null ?
                        new HashMap<>(templateVariables) : new HashMap<>())
                .maxRetryCount(emailProperties.getMaxRetryCount())
                .build();

        sendToQueue(message);
    }

    /**
     * 实际发送邮件（同步）
     */
    @Override
    public boolean sendEmail(EmailMessageDTO emailMessage) {
        log.info("开始发送邮件: messageId={}, to={}",
                emailMessage.getMessageId(), maskEmail(emailMessage.getTo()));

        try {
            MimeMessage mimeMessage = buildMimeMessage(emailMessage);
            javaMailSender.send(mimeMessage);

            log.info("邮件发送成功: messageId={}, to={}",
                    emailMessage.getMessageId(), maskEmail(emailMessage.getTo()));
            return true;

        } catch (Exception e) {
            log.error("邮件发送失败: messageId={}, to={}, error={}",
                    emailMessage.getMessageId(), maskEmail(emailMessage.getTo()),
                    e.getMessage(), e);
            return false;
        }
    }

    /**
     * 构建MimeMessage
     */
    private MimeMessage buildMimeMessage(EmailMessageDTO message) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, emailProperties.getDefaultEncoding());

        helper.setTo(message.getTo());
        helper.setFrom(emailProperties.getFromAddress());
        helper.setSubject(message.getSubject());
        helper.setText(message.getContent(), true);
        helper.setSentDate(message.getSentDate());

        return mimeMessage;
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            log.error("模板渲染失败: template={}, error={}", templateName, e.getMessage(), e);
            throw new BaseException(500, "模板渲染失败: {}" + templateName);
        }
    }

    /**
     * 发送到RabbitMQ
     */
    private void sendToQueue(EmailMessageDTO message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message,
                    msg -> {
                        msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        msg.getMessageProperties().setExpiration(emailProperties.getMessageTtl().toString());
                        // 添加消息头用于追踪
                        msg.getMessageProperties().setHeader("messageId", message.getMessageId());
                        msg.getMessageProperties().setHeader("businessType", message.getBusinessType());
                        return msg;
                    }
            );

            log.info("邮件任务已加入队列: messageId={}, type={}, retry={}",
                    message.getMessageId(), message.getBusinessType(), message.getRetryCount());

        } catch (Exception e) {
            log.error("邮件任务加入队列失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            throw new EmailSendFailureException();
        }
    }

    /**
     * 验证并预处理消息
     */
    private void prepareMessage(EmailMessageDTO message) {

        // 设置默认值
        if (message.getMaxRetryCount() == null) {
            message.setMaxRetryCount(emailProperties.getMaxRetryCount());
        }
        if (message.getSentDate() == null) {
            message.setSentDate(new Date());
        }
    }

    /**
     * 邮箱脱敏
     */
    public String maskEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, Math.min(3, atIndex));
        return prefix + "***" + email.substring(atIndex);
    }
}