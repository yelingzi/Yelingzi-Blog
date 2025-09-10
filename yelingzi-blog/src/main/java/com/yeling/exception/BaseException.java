package com.yeling.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final int code;
    private final HttpStatus httpStatus;

    public BaseException(String message) {
        super(message);
        this.code = 500;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = codeToHttpStatus(code);
    }

    public BaseException(int code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    /**
     * 将整数状态码转换为HttpStatus枚举
     * @param code HTTP状态码
     * @return 对应的HttpStatus，如果找不到则返回INTERNAL_SERVER_ERROR
     */
    private HttpStatus codeToHttpStatus(int code) {
        try {
            return HttpStatus.valueOf(code);
        } catch (IllegalArgumentException e) {
            // 如果状态码不在HttpStatus枚举中，返回一个默认值
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
