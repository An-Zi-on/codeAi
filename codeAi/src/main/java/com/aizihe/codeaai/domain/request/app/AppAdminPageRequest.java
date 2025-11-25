package com.aizihe.codeaai.domain.request.app;

import com.aizihe.codeaai.domain.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员分页查询应用请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppAdminPageRequest extends PageRequest {

    private Long id;

    private String appName;

    private String cover;

    private String initPrompt;

    private String codeGenType;

    private String deployKey;

    private Integer priority;

    private Long userId;

    private Integer isDelete;
}

