package com.aizihe.codeaai.core.save;

import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import java.io.File;

public class HtmlSaveFileTemplate extends CodeSaveFileTemplate<SingleFileGenerateResult> {
    @Override
    protected File saveFile(SingleFileGenerateResult result, String uniqueDir) {
        //构建子目录
        String baseDirPath =buildUniqueDir();
        //在子目录下写入生成的 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
        return new File(baseDirPath);
    }

    @Override
    protected CodeGenTypeEnum getFileType() {
        return CodeGenTypeEnum.GEN_TYPE_HTML;
    }
}
