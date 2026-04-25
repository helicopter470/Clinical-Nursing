package com.example.controller;

import com.example.ai.AiChatRequest;
import com.example.ai.AiChatResponse;
import com.example.ai.AiClient;
import com.example.ai.ChatMessage;
import com.example.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiClient aiClient;

    public AiController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @PostMapping("/chat")
    public Result chat(@Valid @RequestBody AiChatRequest req) {
        String message = req.getMessage();

        List<ChatMessage> safeHistory = new ArrayList<>();
        if (req.getHistory() != null) {
            for (ChatMessage m : req.getHistory()) {
                if (m == null) continue;
                if (m.getRole() == null || m.getContent() == null) continue;
                // 简单截断，避免超长/滥用
                String content = m.getContent();
                if (content.length() > 2000) content = content.substring(0, 2000);

                ChatMessage nm = new ChatMessage();
                nm.setRole(m.getRole());
                nm.setContent(content);
                safeHistory.add(nm);
            }
        }

        // 核心 system prompt：改为“单段结论”输出，便于前端直接展示
        String systemPrompt = "你是临床护理系统里的AI医疗知识助手。\n"
                + "任务：基于用户问题给出健康科普/护理建议，不要做确定性诊断，不要开具或调整处方。\n"
                + "输出要求（必须遵守）：\n"
                + "1) 只输出一段中文自然语言，不要标题/不要分段/不要列表/不要使用【结论】等标签；\n"
                + "2) 内容尽量控制在80-150字，先给最重要的结论与行动建议；\n"
                + "3) 如信息不足，用同一段话在末尾补充2-3个关键追问；\n"
                + "4) 段末追加一句简短免责声明：仅用于健康科普与护理学习，不替代诊断与处方，紧急情况立即就医。\n";

        String answer = aiClient.chat(systemPrompt, safeHistory, message);
        return Result.success(new AiChatResponse(answer));
    }
}
