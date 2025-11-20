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
        String result = aiCodeHelperService.chat("你好，我是哈吉蜂");
        System.out.println(result);
        String result2 = aiCodeHelperService.chat("你好我是谁,我喜欢干嘛");
        System.out.println(result2);


    }
}
