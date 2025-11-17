package com.aizihe.codeaai.domain.common;
import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageRequest {

    /**
     * 当前第几页
     * 默认第一页
     */
    private Integer current = 1;

    /**
     * 每页记录数
     * 默认每页十条记录
     */
    private Integer size = 10;

}
