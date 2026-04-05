package com.aizihe.codeaai.core.parser;


/**
 * @param
 * @return
 */
public interface CodeParser <T>{
    /**
     * 代码解析
     * @param codeContent 内容
     * @return
     */
     T parseCode(String codeContent);
}
