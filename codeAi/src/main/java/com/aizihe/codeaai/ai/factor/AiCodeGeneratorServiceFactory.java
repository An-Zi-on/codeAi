package com.aizihe.codeaai.ai.factor;


import com.aizihe.codeaai.ai.service.AiCodeGeneratorService;
import com.aizihe.codeaai.core.tools.FileWriteTool;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.aizihe.codeaai.ai.model.common.AIConstant.MAX_MESSAGE_COUNT;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource(name = "openAiStreamingChatModel")
    private  StreamingChatModel streamingChatModel;

    @Resource
    private StreamingChatModel openAiStreamingChatModel;
    @Resource
    private  RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * caffeine 缓存AI实例服务
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）这个方法是为了兼容历史逻辑
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        // 缓存中有则 从缓存中获取  如果缓存中没有则在创建一个新的ai实例服务
        return getAiCodeGeneratorService(appId,CodeGenTypeEnum.GEN_TYPE_HTML);
    }

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public  AiCodeGeneratorService getAiCodeGeneratorService(Long appId ,CodeGenTypeEnum codeGenTypeEnum) {
        // 缓存中有则 从缓存中获取  如果缓存中没有则在创建一个新的ai实例服务
        String cacheKey = buildCacheKey(appId,codeGenTypeEnum);
        return serviceCache.get(cacheKey,k -> createAiCodeGeneratorService(appId,codeGenTypeEnum));
    }

    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenTypeEnum) {
        return appId+"_"+codeGenTypeEnum.getValue();
    }

    /**
     * 根据appid 获取服务
     * @param appId
     * @return
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId,CodeGenTypeEnum codeGenTypeEnum) {
        log.info("为 appId:{} 创建新的 AI 服务实例",appId);
        //加载缓存
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(MAX_MESSAGE_COUNT)
                .build();
        //缓存加载历史消息
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,MAX_MESSAGE_COUNT);
        //根据类型创建对应的service实例
        switch (codeGenTypeEnum) {
            case VUE_PROJECT -> {
               return AiServices.builder(AiCodeGeneratorService.class)
                        .streamingChatModel(streamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(new FileWriteTool())
                        .hallucinatedToolNameStrategy(
                                result -> ToolExecutionResultMessage.from(result,"Error: there is no call "+ result.name())
                        )
                        .build();
            }
            case GEN_TYPE_HTML, GEN_MULTI_FILE-> {
                return AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        // 根据 id 构建独立的对话记忆
                        .chatMemory(chatMemory)
                        .build();
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持该类型的工程代码");
        }
    }

    /**
     * 默认提供一个 Bean
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }
    
}
