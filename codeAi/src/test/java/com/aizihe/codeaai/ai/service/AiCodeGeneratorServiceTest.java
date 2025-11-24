package com.aizihe.codeaai.ai.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
   AiCodeGeneratorService aiCodeGeneratorService;
    @Test
    void generateSignalCode() {
        aiCodeGeneratorService.generateSignalCode("测试");
    }

    @Test
    void generateMultiCode() {
        aiCodeGeneratorService.generateMultiCode("我现在就是测试,你不需生成代码,复述我这段话 :dasdasdadadddddddddddddddddddddddddddddddddddddddddddddddddddddd");
    }
}