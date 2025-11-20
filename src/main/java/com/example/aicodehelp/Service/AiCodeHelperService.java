package com.example.aicodehelp.Service;

import dev.langchain4j.service.SystemMessage;

public interface AiCodeHelperService {
    /**
     * 预设prompt
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
}
