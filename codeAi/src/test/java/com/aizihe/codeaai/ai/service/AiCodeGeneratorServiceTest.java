package com.aizihe.codeaai.ai.service;

import com.aizihe.codeaai.ai.AiCodeGeneratorFacade;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;
    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateSignalCode() {
        //System.out.println(aiCodeGeneratorService.generateSignalCode(1L, "我叫什么"));
        System.out.println("11111");
    }

    @Test
    void generateMultiCode() {
        Flux<String> stringFlux = aiCodeGeneratorService.generateMultiCode("我现在就是测试,你不需生成代码,复述我这段话 :dasdasdadadddddddddddddddddddddddddddddddddddddddddddddddddddddd");
    }
    @Test
    void generateCode(){
        //Flux<String> file = aiCodeGeneratorFacade.generateCode("生成一个用户注册登入个人中心的用户前端页面", CodeGenTypeEnum.GEN_TYPE_HTML);
        //System.out.println(file);
    }
    @Test
    void generateAndSaveCodeStream() {
        //Flux<String> codeStream = aiCodeGeneratorFacade.generateCode("任务记录网站", CodeGenTypeEnum.GEN_MULTI_FILE);
        // 阻塞等待所有数据收集完成
      //  List<String> result = codeStream.collectList().block();
        // 验证结果
        //Assertions.assertNotNull(result);
        //String completeContent = String.join("", result);
        //Assertions.assertNotNull(completeContent);
    }

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                "简单的任务记录网站，总代码量不超过 200 行",
                 1L,CodeGenTypeEnum.VUE_PROJECT);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

}