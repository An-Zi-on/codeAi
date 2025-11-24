package com.aizihe.codeaai.ai.service;

import com.aizihe.codeaai.ai.core.AiCodeGeneratorFacade;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import com.aizihe.codeaai.ai.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
   AiCodeGeneratorService aiCodeGeneratorService;
    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Test
    void generateSignalCode() {
        SingleFileGenerationResult  result= aiCodeGeneratorService.generateSignalCode("测试");
        System.out.println(result);
    }

    @Test
    void generateMultiCode() {
        aiCodeGeneratorService.generateMultiCode("我现在就是测试,你不需生成代码,复述我这段话 :dasdasdadadddddddddddddddddddddddddddddddddddddddddddddddddddddd");
    }
    @Test
    void generateCode(){
        File file = aiCodeGeneratorFacade.generateCode("生成一个用户注册登入个人中心的用户前端页面", CodeGenTypeEnum.GEN_TYPE_HTML);
        System.out.println(file.getAbsoluteFile());
    }
}