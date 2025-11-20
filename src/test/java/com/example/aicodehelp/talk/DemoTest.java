package com.example.aicodehelp.talk;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoTest {

    @Resource
    Demo demo;
    @Test
    void chat() {
        demo.chat("测试");
    }
}