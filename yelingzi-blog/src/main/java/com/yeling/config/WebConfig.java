package com.yeling.config;

import com.yeling.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，覆盖所有 API 路径
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/api/admin/**", "/api/user/**")
                .excludePathPatterns("/api/user/login", "/api/user/reg");
    }
}
