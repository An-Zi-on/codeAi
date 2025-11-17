package com.aizihe.codeaAI.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Text {
    /**
     * 健康测试
     * @return
     */
    @PostMapping("/health")
    public String healthText(){
        return  "health text";
    }
}
