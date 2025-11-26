package com.aizihe.codeaai.domain.request.chathistory;

import lombok.Data;

/**
 * 保存对话消息请求
 */
@Data
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

