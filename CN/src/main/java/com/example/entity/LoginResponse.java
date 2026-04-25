package com.example.entity;

public class LoginResponse {
    private Account user;
    private String accessToken;
    private String tokenType = "Bearer";

    public LoginResponse() {
    }

    public LoginResponse(Account user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
