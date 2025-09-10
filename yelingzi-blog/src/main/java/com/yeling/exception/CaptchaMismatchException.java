package com.yeling.exception;

import org.springframework.http.HttpStatus;

public class CaptchaMismatchException extends BaseException {
    public CaptchaMismatchException() {
        super(401, HttpStatus.UNAUTHORIZED, "验证码错误");
    }
}