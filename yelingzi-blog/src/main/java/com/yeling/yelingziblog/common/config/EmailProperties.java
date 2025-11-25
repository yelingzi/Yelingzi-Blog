package com.yeling.yelingziblog.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailProperties {
    private Integer maxRetryCount = 3;
    private Integer messageTtl = 600000; // 10分钟
    private String fromAddress;
    private String defaultEncoding = "UTF-8";
}
