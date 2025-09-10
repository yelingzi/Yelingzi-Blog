package com.yeling.config;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;


@Configuration
public class Ip2RegionConfig {

    @Bean(destroyMethod = "close")
    public Searcher searcher() throws IOException {
        // 从classpath加载xdb文件
        ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
        byte[] bytes;
        try (InputStream inputStream = resource.getInputStream()) {
            bytes = inputStream.readAllBytes();
        }
        // 使用字节数组创建searcher对象
        return Searcher.newWithBuffer(bytes);
    }
}
