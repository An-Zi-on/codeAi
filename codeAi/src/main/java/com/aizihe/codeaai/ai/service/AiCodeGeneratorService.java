package com.aizihe.codeaai.ai.service;

import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {
    /**
     * 单文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "codeAi/src/main/resources/system-prompt/single-file-prompt.txt")
    String generateSignalCode(String userMessage);

    /**
     * 多文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "codeAi/src/main/resources/system-prompt/multi-file-prompt")
    String generateMultiCode(String userMessage);
}
