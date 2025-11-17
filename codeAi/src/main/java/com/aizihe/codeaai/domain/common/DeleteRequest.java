package com.aizihe.codeaai.domain.common;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * 通用删除请求类
 */
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
