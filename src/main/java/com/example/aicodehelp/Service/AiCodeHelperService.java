package com.example.aicodehelp.Service;

import com.alibaba.dashscope.common.Message;
import dev.langchain4j.service.SystemMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface AiCodeHelperService {
    //自己实现会话记忆
    public static final  Map<String, List<Message>> conversationHistory = new HashMap<>();

    /**
     * 预设prompt
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);

    public default String chat(String message, String userId){
        //获取用户历史记录
        List<Message> history = conversationHistory.getOrDefault(userId, new ArrayList<>());
        //添加用户新消息
        Message usermessage = Message.builder().role("user").content(message).build();
        StringBuilder context = new StringBuilder();
        history.add(usermessage);
        for (Message msg : history){
            context.append(msg.getRole()).append(":").append(msg.getContent()).append("\n");
        }
        //调用api
        String response = chat(context.toString());
        Message aiMessage = Message.builder().role("assistant").content(response).build();
        //保存AI回复到历史
        history.add(aiMessage);
        conversationHistory.put(userId,history);
        return response;
    }
}
