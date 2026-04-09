package com.aizihe.codeaai.core.save;

import com.aizihe.codeaai.ai.model.MultiFileGenerateResult;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;

public class MultiSaveFileTemplate extends  CodeSaveFileTemplate<MultiFileGenerateResult>{
    @Override
        protected void saveFile(MultiFileGenerateResult result, String uniqueDir, Long appId) {
        String baseDirPath = buildUniqueDir(appId);
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
        writeToFile(baseDirPath, "style.css", result.getCssContent());
        writeToFile(baseDirPath, "script.js", result.getJsContent());
    }

    @Override
    protected CodeGenTypeEnum getFileType() {
        return CodeGenTypeEnum.GEN_MULTI_FILE;
    }
}
