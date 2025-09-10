package com.yeling.config;

import com.yeling.entity.ErrorResponse;
import com.yeling.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 统一错误响应格式


    // 构建错误响应实体
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, int code, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message));
    }

    // 处理所有验证异常
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        String message;

        if (ex instanceof MethodArgumentNotValidException validEx) {
            message = validEx.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof ConstraintViolationException violationEx) {
            message = violationEx.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
        } else {
            message = "参数验证失败";
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, 400, message);
    }

    // 处理所有业务异常
    @ExceptionHandler({
            TooManyAttemptsException.class,
            CaptchaExpiredException.class,
            CaptchaMismatchException.class,
            UserNotFoundException.class,
            InvalidCredentialsException.class,
            EmailAlreadyRegisteredException.class,
            EmailNotRegisteredException.class,
            EmailSendFailureException.class,
            JwtExpiredException.class,
            JwtInvalidException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(Exception ex) {
        int code = 500;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // 根据异常类型设置状态码
        if (ex instanceof TooManyAttemptsException) {
            code = 429;
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (ex instanceof CaptchaExpiredException ||
                ex instanceof CaptchaMismatchException) {
            code = 401;
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof UserNotFoundException) {
            code = 404;
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof InvalidCredentialsException ||
                ex instanceof JwtExpiredException ||
                ex instanceof JwtInvalidException) {
            code = 401;
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof EmailAlreadyRegisteredException ||
                ex instanceof EmailNotRegisteredException) {
            code = 400;
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof EmailSendFailureException) {
            code = 500;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 特殊处理JWT异常消息
        String message = ex.getMessage();
        if (ex instanceof JwtExpiredException) {
            message = "accessTokenExpired";
        } else if (ex instanceof JwtInvalidException) {
            message = "accessTokenInvalid";
        }

        return buildErrorResponse(status, code, message);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        return buildErrorResponse(ex.getHttpStatus(), ex.getCode(), ex.getMessage());
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("系统异常: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 500, "系统错误，请稍后再试");
    }
}
