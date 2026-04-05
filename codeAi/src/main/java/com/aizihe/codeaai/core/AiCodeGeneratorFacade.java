package com.aizihe.codeaai.core;

import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.ai.factor.AiCodeGeneratorServiceFactory;
import com.aizihe.codeaai.ai.model.MultiFileGenerateResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;
import com.aizihe.codeaai.core.parser.CodeParserExecutor;
import com.aizihe.codeaai.core.save.CodeSaveExecutor;
import com.aizihe.codeaai.core.save.HtmlSaveFileTemplate;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.core.parser.CodeParser;
import com.aizihe.codeaai.core.parser.ParseHtmlCode;
import com.aizihe.codeaai.core.parser.ParseMultiFileCode;
import com.aizihe.codeaai.ai.service.AiCodeGeneratorService;
import com.aizihe.codeaai.ai.utils.CodeFileSaver;
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

    AiCodeGeneratorService aiCodeGeneratorService ;

    @Resource
    AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    public static final CodeParser<SingleFileGenerateResult> singleCode = new ParseHtmlCode();
    public static final CodeParser<MultiFileGenerateResult> multiFileCode = new ParseMultiFileCode();

    /**
     * 同一生成入口：根据类型生成并保存代码
     * @param message
     * @param codeGenTypeEnum
     * @param appId
     * @return
     */
    public Flux<String>  generateCode(String message, CodeGenTypeEnum codeGenTypeEnum,Long appId){
        ThrowUtils.throwIf(codeGenTypeEnum == null,ErrorCode.PARAMS_ERROR,"生成类型不存在");
        return  switch (codeGenTypeEnum) {
            case GEN_TYPE_HTML -> generateAndeSaveHtmlCodeStream(message,appId);
            case GEN_MULTI_FILE -> generateAndSaveMultiCodeStream(message,appId);
            default -> {
                String msg = "不支持生成"+codeGenTypeEnum.getMessage()+"文件";
                throw  new  BusinessException(ErrorCode.SYSTEM_ERROR,msg);
            }
        };
    }



    private Flux<String> generateAndeSaveHtmlCodeStream(String userMessage,Long appId){
        aiCodeGeneratorService =aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> result = aiCodeGeneratorService.generateSignalCode(userMessage);
        // 使用线程安全的 StringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        //时时收集代码片段，使用同步块确保线程安全
        return result
                .doOnNext(chunk -> {
                    //时时收集代码块
                    if (chunk != null) {
                        synchronized (stringBuilder) {
                            stringBuilder.append(chunk);
                        }
                    }
                })
                .doOnTerminate(() -> {
                    // 无论是正常完成还是异常终止，都尝试保存
                    synchronized (stringBuilder) {
                        try {
                            String completeHtmlCode = stringBuilder.toString();
                            if (!completeHtmlCode.trim().isEmpty()) {
                                Object object = CodeParserExecutor.parse(completeHtmlCode,CodeGenTypeEnum.GEN_TYPE_HTML);
                                File file = CodeSaveExecutor.execute(object, CodeGenTypeEnum.GEN_TYPE_HTML);
                                System.out.println("保存成功,路径为:"+file.getAbsolutePath());
                            } else {
                                System.out.println("警告: HTML 代码内容为空，未保存文件");
                            }
                        } catch (Exception e){
                            System.out.println("文件保存失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    private Flux<String> generateAndSaveMultiCodeStream(String userMessage,Long appId){
        //工厂类中取数据
         aiCodeGeneratorService =aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String>  result = aiCodeGeneratorService.generateMultiCode(userMessage);
        // 使用线程安全的 StringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        return result
                .doOnNext(chunk -> {
                    if (chunk != null) {
                        synchronized (stringBuilder) {
                            stringBuilder.append(chunk);
                        }
                    }
                })
                .doOnTerminate(() -> {
                    // 无论是正常完成还是异常终止，都尝试保存
                    synchronized (stringBuilder) {
                        try {
                            String completeMultiCode = stringBuilder.toString();
                            if (!completeMultiCode.trim().isEmpty()) {
                                Object object = CodeParserExecutor.parse(completeMultiCode,CodeGenTypeEnum.GEN_MULTI_FILE);
                                File file = CodeSaveExecutor.execute(object, CodeGenTypeEnum.GEN_MULTI_FILE);
                                System.out.println("保存文件成功,保存路径为:"+file.getAbsolutePath());
                            } else {
                                System.out.println("警告: 多文件代码内容为空，未保存文件");
                            }
                        }catch (Exception e){
                            System.out.println("保存文件失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    //    /**
//     * 保存单文件
//     * @param message 用户信息
//     * @return 生成的文件
//     */
//    public  File  generateSingleCode(String message){
//        SingleFileGenerateResult result = aiCodeGeneratorService.generateSignalCode(message);
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
