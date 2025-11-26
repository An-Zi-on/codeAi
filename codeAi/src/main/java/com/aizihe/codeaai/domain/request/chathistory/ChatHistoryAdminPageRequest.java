package com.aizihe.codeaai.domain.request.chathistory;

import com.aizihe.codeaai.domain.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员对话历史查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryAdminPageRequest extends PageRequest {

    /**
     * 应用 id（可选）
     */
    private Long appId;

    /**
     * 用户 id（可选）
     */
    private Long userId;

    /**
     * 消息类型 user / ai （可选）
     */
    private String messageType;
}

