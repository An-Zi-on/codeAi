package com.aizihe.codeaai.core.parser;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;


public class CodeParserExecutor {

    public static final ParseHtmlCode PARSE_HTML_CODE = new ParseHtmlCode();

    public static final ParseMultiFileCode PARSE_MULTI_FILE_CODE = new ParseMultiFileCode();

    /**
     * 代码解析解析执行器
     * @param content 解析内容
     * @param codeGenTypeEnum 解析文件类型
     * @return 解析后的代码
     */
    public static Object parse(String content, CodeGenTypeEnum codeGenTypeEnum){
        return  switch (codeGenTypeEnum){
            case GEN_MULTI_FILE -> PARSE_MULTI_FILE_CODE.parseCode(content);
            case GEN_TYPE_HTML -> PARSE_HTML_CODE.parseCode(content);
            default -> throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持改类型的代码解析");
        };

    }

}
