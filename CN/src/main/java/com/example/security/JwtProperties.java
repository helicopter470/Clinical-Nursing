package com.example.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * HS256/HS384/HS512 secret，建议 >= 256bit。
     */
    private String secret;

    /** access token 过期秒数 */
    private long accessExpireSeconds = 900;

    /** refresh token 过期秒数 */
    private long refreshExpireSeconds = 604800;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessExpireSeconds() {
        return accessExpireSeconds;
    }

    public void setAccessExpireSeconds(long accessExpireSeconds) {
        this.accessExpireSeconds = accessExpireSeconds;
    }

    public long getRefreshExpireSeconds() {
        return refreshExpireSeconds;
    }

    public void setRefreshExpireSeconds(long refreshExpireSeconds) {
        this.refreshExpireSeconds = refreshExpireSeconds;
    }
}
