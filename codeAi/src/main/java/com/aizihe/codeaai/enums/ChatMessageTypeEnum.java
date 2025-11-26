package com.aizihe.codeaai.enums;

import lombok.Getter;

/**
 * 对话消息类型枚举
 */
@Getter
public enum ChatMessageTypeEnum {

    USER("用户消息", "user"),
    AI("AI 消息", "ai");

    private final String text;
    private final String value;

    ChatMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static ChatMessageTypeEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ChatMessageTypeEnum typeEnum : ChatMessageTypeEnum.values()) {
            if (typeEnum.value.equalsIgnoreCase(value.trim())) {
                return typeEnum;
            }
        }
        return null;
    }
}

