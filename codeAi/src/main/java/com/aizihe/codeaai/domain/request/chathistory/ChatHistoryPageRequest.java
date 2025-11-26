package com.aizihe.codeaai.domain.request.chathistory;

import com.aizihe.codeaai.domain.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用侧对话历史查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryPageRequest extends PageRequest {

    /**
     * 应用 id
     */
    private Long appId;
}

