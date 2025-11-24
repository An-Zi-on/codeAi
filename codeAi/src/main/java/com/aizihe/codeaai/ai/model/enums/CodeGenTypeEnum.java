package com.aizihe.codeaai.ai.model.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @param
 * @return
 */
@Getter
public enum CodeGenTypeEnum {
    GEN_MULTI_FILE("多文件","multi_file"),
    GEN_TYPE_HTML("单文件","html");
    private final String message;
    private final String value;
    CodeGenTypeEnum(String message ,String value){
        this.message = message;
        this.value =value;
    }

    public  static  CodeGenTypeEnum getByValue(String value){
        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {
            if (codeGenTypeEnum.value.equals(value)){
                return codeGenTypeEnum;
            }
        }
        return  null;
    }
}
