package com.aizihe.codeaai.domain.request.chathistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * 保存对话消息请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryMessageSaveRequest {

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型 user / ai
     */
    private String messageType;
}

