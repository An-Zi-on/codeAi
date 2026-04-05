package com.aizihe.codeaai.core.save;

import com.aizihe.codeaai.ai.model.MultiFileGenerateResult;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;

import java.io.File;

public class MultiSaveFileTemplate extends  CodeSaveFileTemplate<MultiFileGenerateResult>{
    @Override
    protected File saveFile(MultiFileGenerateResult result, String uniqueDir) {
        String baseDirPath = buildUniqueDir();
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
        writeToFile(baseDirPath, "style.css", result.getCssContent());
        writeToFile(baseDirPath, "script.js", result.getJsContent());
        return new File(baseDirPath);
    }

    @Override
    protected CodeGenTypeEnum getFileType() {
        return CodeGenTypeEnum.GEN_MULTI_FILE;
    }
}
