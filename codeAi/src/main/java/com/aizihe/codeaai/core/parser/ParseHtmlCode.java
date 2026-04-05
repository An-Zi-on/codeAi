package com.aizihe.codeaai.core.parser;

import com.aizihe.codeaai.ai.utils.CodeParserUtil;
import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;

/**
 *  解析Html代码
 */
public class ParseHtmlCode implements CodeParser<SingleFileGenerateResult>{
    @Override
    public SingleFileGenerateResult parseCode(String codeContent) {
        SingleFileGenerateResult result = new SingleFileGenerateResult();
        // 提取 HTML 代码
        String htmlCode = CodeParserUtil.extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlContent(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlContent(codeContent.trim());
        }
        return result;
    }
}
