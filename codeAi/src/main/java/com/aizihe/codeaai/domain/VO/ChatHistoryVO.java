package com.aizihe.codeaai.domain.VO;

import com.aizihe.codeaai.domain.entity.ChatHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long appId;

    private Long userId;

    private String message;

    private String messageType;

    private LocalDateTime createTime;

    public static ChatHistoryVO fromEntity(ChatHistory entity) {
        if (entity == null) {
            return null;
        }
        return ChatHistoryVO.builder()
                .id(entity.getId())
                .appId(entity.getAppId())
                .userId(entity.getUserId())
                .message(entity.getMessage())
                .messageType(entity.getMessageType())
                .createTime(entity.getCreateTime())
                .build();
    }
}

