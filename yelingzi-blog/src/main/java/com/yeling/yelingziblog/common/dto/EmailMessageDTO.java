package com.yeling.yelingziblog.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一邮件消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class EmailMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "收件人不能为空")
    @Email(message = "邮箱格式不正确")
    private String to;

    @NotBlank(message = "邮件主题不能为空")
    @Size(max = 200, message = "邮件主题长度不能超过200字符")
    private String subject;

    @NotBlank(message = "邮件内容不能为空")
    private String content;

    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Builder.Default
    private Map<String, Object> businessData = new HashMap<>();

    @Builder.Default
    private Date sentDate = new Date();

    @Builder.Default
    @Min(value = 0, message = "重试次数不能小于0")
    private Integer retryCount = 0;

    @Builder.Default
    @Min(value = 1, message = "最大重试次数不能小于1")
    private Integer maxRetryCount = 3;

    // 添加消息ID用于追踪
    @Builder.Default
    private String messageId = UUID.randomUUID().toString();

    public static EmailMessageDTO of(String to, String subject, String content, String businessType) {
        return EmailMessageDTO.builder()
                .to(to.trim().toLowerCase()) // 统一格式
                .subject(subject)
                .content(content)
                .businessType(businessType)
                .build();
    }

    public boolean canRetry() {
        return retryCount < maxRetryCount;
    }

    public void incrementRetry() {
        this.retryCount++;
    }
}
