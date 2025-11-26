package com.aizihe.codeaai.domain.request.app;

import lombok.Data;

/**
 * 用户创建应用请求
 */
@Data
public class AppCreateRequest {
    /**
     * 初始化提示词
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;

}

