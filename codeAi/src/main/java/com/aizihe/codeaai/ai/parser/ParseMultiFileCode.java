package com.aizihe.codeaai.ai.parser;

import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.utils.CodeParserUtil;

import static com.aizihe.codeaai.ai.utils.CodeParserUtil.HTML_CODE_PATTERN;

/**
 *  解析多文件代码
 */
public class ParseMultiFileCode implements CodeParser<MultiFileWebsiteResult>{
    @Override
    public MultiFileWebsiteResult parseCode(String codeContent) {
        MultiFileWebsiteResult result = new MultiFileWebsiteResult();
        // 提取各类代码
        String htmlCode = CodeParserUtil.extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = CodeParserUtil.extractCodeByPattern(codeContent,CodeParserUtil.CSS_CODE_PATTERN);
        String jsCode = CodeParserUtil.extractCodeByPattern(codeContent,CodeParserUtil.JS_CODE_PATTERN);
        // 设置HTML代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlContent(htmlCode.trim());
        }
        // 设置CSS代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssContent(cssCode.trim());
        }
        // 设置JS代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsContent(jsCode.trim());
        }
        return result;
    }
}
