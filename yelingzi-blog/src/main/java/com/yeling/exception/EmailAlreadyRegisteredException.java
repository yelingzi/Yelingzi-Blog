package com.yeling.exception;

public class EmailAlreadyRegisteredException extends BaseException {
    public EmailAlreadyRegisteredException() {
        super("邮箱已被注册");
    }
}