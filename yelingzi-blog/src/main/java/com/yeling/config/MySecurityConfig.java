package com.yeling.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
//@EnableWebSecurity //开启SpringSecurity的默认行为
@Slf4j //日志
//@EnableGlobalMethodSecurity(prePostEnabled = true)// 新版不推荐使用这个,这个的主	要功能是开启方法上的鉴权，使用下面这个就可以
@EnableMethodSecurity
// 新版不需要继承WebSecurityConfigurerAdapter
public class MySecurityConfig {
//    // 这个类主要是获取库中的用户信息，交给security
//    @Autowired
//    UserDetailServiceImpl userDetailsService;
    // 这个的类是认证失败处理（我在这里主要是把错误消息以json方式返回）
//    @Autowired
//    private JwtAuthenticationEntryPoint unauthorizedHandler;
//    // 鉴权失败的时候的处理类
//    @Autowired
//    private JwtAccessDeniedHandler jwtAccessDeniedHandler;


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:80",
                "https://www.yeling.top",
                "https://blog.yeling.top"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS")); // 必须包含 OPTIONS
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 核心配置
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http)throws Exception{

        http.cors(cors -> {
            cors.configurationSource(corsConfigurationSource());
        })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }




}
