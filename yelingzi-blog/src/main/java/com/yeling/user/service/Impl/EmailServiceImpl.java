package com.yeling.user.service.Impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.yeling.config.RabbitMQConfig;
import com.yeling.exception.EmailSendFailureException;
import com.yeling.user.domian.dto.EmailMessage;
import com.yeling.user.mapper.UserMapper;
import com.yeling.user.service.EmailService;
import com.yeling.utils.VerCodeGenerateUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    /**
     * 注入邮件工具类
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * redis缓存
     */
    @Autowired // 自动注入 RedisTemplate
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Thymeleaf模板
     */
    private final SpringTemplateEngine templateEngine;

    public EmailServiceImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        // 初始化LoginRateLimiter
    }

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 发送邮件验证码（异步方式）
     */
    public void sendVerificationCodeAsync(String verifyCode, String email) {
        // 构建邮件内容
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(verifyCode.split("")));
        String content = templateEngine.process("EmailVerificationCode.html", context);

        // 创建邮件消息
        EmailMessage emailMessage = new EmailMessage(
                email,
                "【叶玲子的网页】验证码",
                content,
                verifyCode,
                new Date()
        );

        // 发送到RabbitMQ队列
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    emailMessage,
                    message -> {
                        // 设置消息持久化
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        // 设置消息过期时间（10分钟）
                        message.getMessageProperties().setExpiration("600000");
                        return message;
                    }
            );
            log.info("邮件发送任务已加入队列，收件人: {}", email);
        } catch (Exception e) {
            log.error("邮件发送任务加入队列失败: {}", e.getMessage());
            throw new EmailSendFailureException();
        }
    }

    /**
     * 实际发送邮件的方法（由消费者调用）
     */
    public boolean sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(emailMessage.getSubject());
            helper.setFrom(sendMailer);
            helper.setTo(emailMessage.getTo());
            helper.setSentDate(emailMessage.getSentDate());
            helper.setText(emailMessage.getContent(), true);

            javaMailSender.send(mimeMessage);
            log.info("发送邮件成功: {} --> {}", sendMailer, emailMessage.getTo());
            return true;
        } catch (MessagingException e) {
            log.error("发送邮件失败: {}", e.getMessage());
            return false;
        }
    }


}



