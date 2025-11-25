package com.aizihe.codeaai.domain.request.app;

import lombok.Data;

/**
 * 用户更新自己应用请求
 */
@Data
public class AppUpdateMyRequest {

    /**
     * 应用 id
     */
    private Long id;

    /**
     * 新的应用名称
     */
    private String appName;
}

