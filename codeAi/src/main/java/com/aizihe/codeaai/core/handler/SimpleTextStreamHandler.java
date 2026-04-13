package com.aizihe.codeaai.core.handler;

import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.enums.ChatMessageTypeEnum;
import com.aizihe.codeaai.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML, MULTI_FILE）
     * 直接收集完整的文本响应
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId, User loginUser) {
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originFlux
                .map(chunk -> {
                    // 收集AI响应内容
                    aiResponseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流式响应完成后，添加AI消息到对话历史
                    String aiResponse = aiResponseBuilder.toString();
                    chatHistoryService.saveMessage(
                            ChatHistoryMessageSaveRequest.builder()
                                    .appId(appId)
                                    .message(aiResponse)
                                    .messageType(ChatMessageTypeEnum.AI.getValue())
                                    .build(),
                            UserVO.fromEntity(loginUser));
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.saveMessage(                            ChatHistoryMessageSaveRequest.builder()
                                    .appId(appId)
                                    .message(errorMessage)
                                    .messageType(ChatMessageTypeEnum.AI.getValue())
                                    .build(),
                            UserVO.fromEntity(loginUser));
                });
    }
}
