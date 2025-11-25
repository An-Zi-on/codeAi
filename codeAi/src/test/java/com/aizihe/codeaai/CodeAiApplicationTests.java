package com.aizihe.codeaai;

import com.aizihe.codeaai.ThrowUtils.RedisService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeAiApplicationTests {

    @Resource
    RedisService redisService;
    @Test
    void contextLoads() {
        redisService.setCacheObject("text","测试");
    }

}
