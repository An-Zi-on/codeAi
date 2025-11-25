package com.aizihe.codeaai.ai.service;

import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {


    /**
     * 单文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "/system-prompt/single-file-prompt.txt")
    Flux<String> generateSignalCode(String userMessage);

    /**
     * 多文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt/multi-file-prompt")
    Flux<String> generateMultiCode(String userMessage);
}
