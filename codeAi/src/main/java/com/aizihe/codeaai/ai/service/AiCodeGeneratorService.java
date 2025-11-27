package com.aizihe.codeaai.ai.service;

import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {


    /**
     * 单文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "/system-prompt/single-file-prompt.txt")
    Flux<String> generateSignalCode(@MemoryId Long memoryId, @UserMessage String userMessage);
    //历史会话  如果加入了历史会话 使用MemoryId来进行存储的话需要会为每一个memoryId 单独进行历史记录存储
    //Flux<String> generateSignalCode(@MemoryId Long memoryId, @UserMessage String userMessage);
    /**
     * 多文件生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt/multi-file-prompt")
    Flux<String> generateMultiCode(String userMessage);
}
