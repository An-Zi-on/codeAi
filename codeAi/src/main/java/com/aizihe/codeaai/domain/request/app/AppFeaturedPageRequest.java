package com.aizihe.codeaai.domain.request.app;

import com.aizihe.codeaai.domain.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 精选应用分页请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppFeaturedPageRequest extends PageRequest {

    /**
     * 名称关键字
     */
    private String nameKeyword;
}

