package com.aizihe.codeaai.domain.request.app;

import lombok.Data;

/**
 * 用户创建应用请求
 */
@Data
public class AppCreateRequest {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 初始化提示词
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 部署标识
     */
    private String deployKey;
}

