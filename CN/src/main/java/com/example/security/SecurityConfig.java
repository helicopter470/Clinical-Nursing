package com.example.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    // 这里不使用 Spring Security，采用自定义 OncePerRequestFilter + FilterRegistrationBean 的方式做轻量 JWT 鉴权
}
