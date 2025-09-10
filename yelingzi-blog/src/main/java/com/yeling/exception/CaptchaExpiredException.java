package com.yeling.exception;

import org.springframework.http.HttpStatus;

public class CaptchaExpiredException extends BaseException {
    public CaptchaExpiredException() {
        super(401, HttpStatus.UNAUTHORIZED, "验证码过期");
    }
}