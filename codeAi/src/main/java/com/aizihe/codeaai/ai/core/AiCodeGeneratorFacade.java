package com.aizihe.codeaai.ai.core;

import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import com.aizihe.codeaai.ai.model.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.ai.parser.CodeParser;
import com.aizihe.codeaai.ai.parser.ParseHtmlCode;
import com.aizihe.codeaai.ai.parser.ParseMultiFileCode;
import com.aizihe.codeaai.ai.service.AiCodeGeneratorService;
import com.aizihe.codeaai.ai.utils.CodeParserUtil;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @param
 * @return
 */

@Service
public class AiCodeGeneratorFacade {
    @Resource
    AiCodeGeneratorService aiCodeGeneratorService ;

    public static final CodeParser singleCode = new ParseHtmlCode();
    public static final CodeParser multiFileCode = new ParseMultiFileCode();

    public Flux<String>  generateCode(String message, CodeGenTypeEnum codeGenTypeEnum){
        ThrowUtils.throwIf(codeGenTypeEnum == null,ErrorCode.PARAMS_ERROR,"生成类型不存在");
        return  switch (codeGenTypeEnum) {
            case GEN_TYPE_HTML -> generateAndeSaveHtmlCodeStream(message);
            case GEN_MULTI_FILE -> generateAndSaveMultiCodeStream(message);
            default -> {
                String msg = "不支持生成"+codeGenTypeEnum.getMessage()+"文件";
                throw  new  BusinessException(ErrorCode.SYSTEM_ERROR,msg);
            }
        };
    }


    public Flux<String> generateAndeSaveHtmlCodeStream(String userMessage){
        Flux<String> result = aiCodeGeneratorService.generateSignalCode(userMessage);
        StringBuilder stringBuilder = new StringBuilder();
        //时时收集代码片段
        return result.doOnNext(stringBuilder::append).doOnComplete(()->{
            try {
                String completeHtmlCode = stringBuilder.toString();
                SingleFileGenerationResult singleFileGenerationResult = (SingleFileGenerationResult) singleCode.parseCode(completeHtmlCode);
                File saveDir = CodeFileSaver.saveHtmlCodeResult(singleFileGenerationResult);
                System.out.println("保存成功,路径为:"+saveDir.getAbsolutePath());
            }catch (Exception e){
                System.out.println("文件保存失败"+e.getMessage());
            }
        });
    }

    public Flux<String> generateAndSaveMultiCodeStream(String userMessage){
        Flux<String>  result = aiCodeGeneratorService.generateMultiCode(userMessage);
        StringBuilder stringBuilder = new StringBuilder();
        return result.doOnNext(stringBuilder::append).doOnComplete(()->{
            try {
                String completeMultiCode = stringBuilder.toString();
                MultiFileWebsiteResult multiFileWebsiteResult = (MultiFileWebsiteResult) multiFileCode.parseCode(completeMultiCode);
                File file = CodeFileSaver.saveMultiFileCodeResult(multiFileWebsiteResult);
                System.out.println("保存文件成功,保存路径为:"+file.getAbsolutePath());
            }catch (Exception e){
                System.out.println("保存文件失败"+e.getMessage());
            }
        });
    }

    //    /**
//     * 保存单文件
//     * @param message 用户信息
//     * @return 生成的文件
//     */
//    public  File  generateSingleCode(String message){
//        SingleFileGenerationResult result = aiCodeGeneratorService.generateSignalCode(message);
//        return CodeFileSaver.saveHtmlCodeResult(result);
//    }
//
//    /**
//     * 保存多文件
//     * @param message 用户信息
//     * @return 生成的文件
//     */
//    public  File  generateMultiCode(String message){
//        MultiFileWebsiteResult result = aiCodeGeneratorService.generateMultiCode(message);
//        return CodeFileSaver.saveMultiFileCodeResult(result) ;
//    }

}
