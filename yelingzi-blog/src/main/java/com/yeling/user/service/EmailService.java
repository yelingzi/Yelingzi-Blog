package com.yeling.user.service;

import com.yeling.user.domian.dto.EmailMessage;

public interface EmailService {

    boolean sendEmail(EmailMessage emailMessage);

    void sendVerificationCodeAsync(String verifyCode, String email);

}
