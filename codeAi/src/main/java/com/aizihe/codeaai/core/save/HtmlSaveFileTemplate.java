package com.aizihe.codeaai.core.save;

import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;

public class HtmlSaveFileTemplate extends CodeSaveFileTemplate<SingleFileGenerateResult> {
    @Override
    protected void saveFile(SingleFileGenerateResult result, String uniqueDir , Long appId) {
        //构建子目录
        String baseDirPath =buildUniqueDir(appId);
        //在子目录下写入生成的 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
    }

    @Override
    protected CodeGenTypeEnum getFileType() {
        return CodeGenTypeEnum.GEN_TYPE_HTML;
    }
}
