package com.example.ai;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AiClient {

    private final AiConfig aiConfig;

    public AiClient(AiConfig aiConfig) {
        this.aiConfig = aiConfig;
    }

    public String chat(String systemPrompt, List<ChatMessage> history, String userMessage) {
        if (aiConfig.getBaseUrl() == null || aiConfig.getBaseUrl().isBlank()) {
            throw new IllegalStateException("AI baseUrl未配置");
        }
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().isBlank()) {
            throw new IllegalStateException("AI apiKey未配置");
        }

        String url = aiConfig.getBaseUrl();
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        url = url + "/chat/completions";

        JSONObject body = new JSONObject();
        body.set("model", aiConfig.getModel());
        body.set("temperature", aiConfig.getTemperature());
        // 限制输出 token（不同模型/厂商口径略有差异，但 OpenAI 兼容接口通常支持）
        if (aiConfig.getMaxTokens() != null && aiConfig.getMaxTokens() > 0) {
            body.set("max_tokens", aiConfig.getMaxTokens());
        }

        JSONArray messages = new JSONArray();
        messages.add(new JSONObject().set("role", "system").set("content", systemPrompt));

        if (history != null) {
            for (ChatMessage m : history) {
                if (m == null) continue;
                String role = m.getRole();
                String content = m.getContent();
                if (role == null || content == null) continue;
                // 只允许 user/assistant/system
                if (!role.equals("user") && !role.equals("assistant") && !role.equals("system")) continue;
                messages.add(new JSONObject().set("role", role).set("content", content));
            }
        }

        messages.add(new JSONObject().set("role", "user").set("content", userMessage));
        body.set("messages", messages);

        HttpResponse resp = HttpRequest.post(url)
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .header("Content-Type", ContentType.JSON.getValue())
                .body(body.toString())
                .execute();

        int status = resp.getStatus();
        String respBody = resp.body();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("AI接口调用失败，HTTP=" + status + ", body=" + respBody);
        }

        JSONObject json = JSONUtil.parseObj(respBody);
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("AI返回为空");
        }
        JSONObject choice0 = choices.getJSONObject(0);
        JSONObject message = choice0.getJSONObject("message");
        if (message == null) {
            throw new IllegalStateException("AI返回缺少message字段");
        }
        String content = message.getStr("content");
        if (content == null) content = "";
        String text = new String(content.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8).trim();

        // 二次兜底：按字符数截断，避免超长输出
        if (aiConfig.getMaxChars() != null && aiConfig.getMaxChars() > 0 && text.length() > aiConfig.getMaxChars()) {
            text = text.substring(0, aiConfig.getMaxChars()) + "…";
        }
        return text;
    }
}
