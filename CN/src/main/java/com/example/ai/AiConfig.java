package com.example.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    /**
     * 模型提供方：openai / aliyun / deepseek ...
     * 当前实现按 OpenAI 兼容接口处理。
     */
    private String provider = "openai";

    /**
     * OpenAI 兼容接口 baseUrl，例如：
     * - https://api.openai.com/v1
     * - https://api.deepseek.com/v1
     */
    private String baseUrl;

    /**
     * API Key（务必只放后端配置，不要放前端）
     */
    private String apiKey;

    /**
     * 模型名称，例如：gpt-4o-mini / deepseek-chat
     */
    private String model = "gpt-4o-mini";

    /**
     * 温度参数（随机性）
     */
    private Double temperature = 0.2;

    /**
     * 限制模型最大输出 token（OpenAI 兼容接口常用参数：max_tokens）
     */
    private Integer maxTokens = 120;

    /**
     * 返回给前端前的“字符数”硬截断（兜底用，避免超长输出）
     */
    private Integer maxChars = 100;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getMaxChars() {
        return maxChars;
    }

    public void setMaxChars(Integer maxChars) {
        this.maxChars = maxChars;
    }
}
