package com.yeling.yelingziblog;

import com.yeling.yelingziblog.export.config.ExportTableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableConfigurationProperties({ExportTableProperties.class})
public class YelingziBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YelingziBlogApplication.class, args);
    }

}
