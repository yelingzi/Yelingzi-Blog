package com.yeling.yelingziblog.common.service;

import com.yeling.yelingziblog.common.dto.EmailMessageDTO;
import com.yeling.yelingziblog.user.dto.EmailMessage;
import jakarta.validation.Valid;

import java.util.Map;

public interface EmailService {

    boolean sendEmail(@Valid EmailMessageDTO emailMessage);

    void sendEmailAsync(EmailMessageDTO emailMessage);

    void sendTemplateEmailAsync(String toEmail, String subject, String templateName,
                                       Map<String, Object> templateVariables, String businessType);

    String maskEmail(String email);
}
