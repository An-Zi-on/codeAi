package com.aizihe.codeaai.ai.core;

import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import com.aizihe.codeaai.ai.model.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.ai.service.AiCodeGeneratorService;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import com.jfinal.template.stat.ast.Switch;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.PublicKey;

/**
 * @param
 * @return
 */

@Service
public class AiCodeGeneratorFacade {
    @Resource
    AiCodeGeneratorService aiCodeGeneratorService ;

    public File  generateCode(String message, CodeGenTypeEnum codeGenTypeEnum){
        ThrowUtils.throwIf(codeGenTypeEnum == null,ErrorCode.PARAMS_ERROR,"生成类型不存在");
        return  switch (codeGenTypeEnum) {
            case GEN_TYPE_HTML -> generateSingleCode(message);
            case GEN_MULTI_FILE -> generateMultiCode(message);
            default -> {
                String msg = "不支持生成"+codeGenTypeEnum.getMessage()+"文件";
                throw  new  BusinessException(ErrorCode.SYSTEM_ERROR,msg);
            }
        };
    }

    /**
     * 保存单文件
     * @param message 用户信息
     * @return 生成的文件
     */
    public  File  generateSingleCode(String message){
        SingleFileGenerationResult result = aiCodeGeneratorService.generateSignalCode(message);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 保存多文件
     * @param message 用户信息
     * @return 生成的文件
     */
    public  File  generateMultiCode(String message){
        MultiFileWebsiteResult result = aiCodeGeneratorService.generateMultiCode(message);
        return CodeFileSaver.saveMultiFileCodeResult(result) ;
    }
}
