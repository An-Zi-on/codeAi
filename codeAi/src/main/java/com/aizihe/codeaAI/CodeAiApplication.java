package com.aizihe.codeaAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class CodeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeAiApplication.class, args);
    }

}
