package com.yeling.exception;

public class EmailNotRegisteredException extends BaseException {
    public EmailNotRegisteredException() {
        super("邮箱未注册");
    }
}
