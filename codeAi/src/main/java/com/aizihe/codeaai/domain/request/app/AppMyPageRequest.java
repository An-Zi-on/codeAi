package com.aizihe.codeaai.domain.request.app;

import com.aizihe.codeaai.domain.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询自己应用请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMyPageRequest extends PageRequest {

    /**
     * 名称关键字
     */
    private String nameKeyword;
}

