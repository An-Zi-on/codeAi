package com.aizihe.codeaai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.aizihe.codeaai.mapper")
public class CodeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeAiApplication.class, args);
    }

}