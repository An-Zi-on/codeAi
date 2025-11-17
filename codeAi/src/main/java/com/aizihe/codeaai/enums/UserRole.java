package com.aizihe.codeaai.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("管理员", "admin"),
    USER("普通用户", "user");

    UserRole(String text, String value) {
        this.text = text;
        this.value = value;
    }

    private String text;
    private String value;


}
