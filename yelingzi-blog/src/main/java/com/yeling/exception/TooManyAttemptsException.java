package com.yeling.exception;

import org.springframework.http.HttpStatus;

public class TooManyAttemptsException extends BaseException {
    public TooManyAttemptsException() {
        super(429, HttpStatus.TOO_MANY_REQUESTS, "访问次数过多，请稍后重试");
    }

    public TooManyAttemptsException(String message) {
        super(429, HttpStatus.TOO_MANY_REQUESTS, message);
    }


}