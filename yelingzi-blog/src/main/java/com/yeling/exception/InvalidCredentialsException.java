package com.yeling.exception;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException(String message) {
        super("用户名或密码错误");
    }
}