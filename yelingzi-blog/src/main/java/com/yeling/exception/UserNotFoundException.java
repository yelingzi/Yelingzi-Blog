package com.yeling.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(404, HttpStatus.NOT_FOUND, "用户不存在");
    }
}
