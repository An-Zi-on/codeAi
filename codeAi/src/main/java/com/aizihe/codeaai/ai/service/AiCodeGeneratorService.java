package com.aizihe.codeaai.ai.service;

import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {
    /**
     * 单文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "/system-prompt/single-file-prompt.txt")
    SingleFileGenerationResult generateSignalCode(String userMessage);

    /**
     * 多文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt/multi-file-prompt")
    MultiFileWebsiteResult generateMultiCode(String userMessage);
}
