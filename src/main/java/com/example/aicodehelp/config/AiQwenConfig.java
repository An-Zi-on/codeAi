//package com.example.aicodehelp.config;
//
//import dev.langchain4j.community.model.dashscope.QwenChatModel;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class AiQwenConfig {
//
//    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
//    private String apiKey;
//
//    @Bean
//    public QwenChatModel qwenChatModel() {
//        return QwenChatModel.builder()
//                .apiKey(apiKey)
//                .modelName("qwen-max")
//                .enableSearch(true)
//                .temperature(0.7F)
//                .maxTokens(4096)
//                .stops(List.of("Hello"))
//                .build();
//    }
//}