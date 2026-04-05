package com.aizihe.codeaai.core.save;

import com.aizihe.codeaai.ai.model.MultiFileGenerateResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;
import com.aizihe.codeaai.core.parser.ParseHtmlCode;
import com.aizihe.codeaai.core.parser.ParseMultiFileCode;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;

import java.io.File;

public class CodeSaveExecutor {
    public static final HtmlSaveFileTemplate HTML_SAVE_FILE_TEMPLATE = new HtmlSaveFileTemplate();
    public  static  final  MultiSaveFileTemplate MULTI_SAVE_FILE_TEMPLATE = new MultiSaveFileTemplate();

    /**
     * 代码解析解析执行器
     * @param content 解析内容
     * @param codeGenTypeEnum 解析文件类型
     * @return 解析后的代码
     */
    public static File execute(Object content, CodeGenTypeEnum codeGenTypeEnum){
        return  switch (codeGenTypeEnum){
            case GEN_MULTI_FILE -> HTML_SAVE_FILE_TEMPLATE.execute((SingleFileGenerateResult) content);
            case GEN_TYPE_HTML -> MULTI_SAVE_FILE_TEMPLATE.execute((MultiFileGenerateResult) content);
            default -> throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持改类型的代码解析");
        };

    }

}
