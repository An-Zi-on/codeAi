package com.aizihe.codeaai;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.aizihe.codeaai.mapper")
// 在启动类中排除embedding的自动装配，因为本项目用不到
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
public class CodeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeAiApplication.class, args);
    }

}