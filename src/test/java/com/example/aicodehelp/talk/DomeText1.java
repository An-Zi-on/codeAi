package com.example.aicodehelp.talk;

import com.example.aicodehelp.Service.AiCodeHelperService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好，我是哈吉蜂,喜欢跑刀阴人");
        System.out.println(result);
    }
}
