package com.aizihe.codeaai.ai;

import cn.hutool.json.JSONUtil;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.ai.factor.AiCodeGeneratorServiceFactory;
import com.aizihe.codeaai.ai.model.MultiFileGenerateResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerateResult;
import com.aizihe.codeaai.ai.model.message.AiResponseMessage;
import com.aizihe.codeaai.ai.model.message.ToolExecutedMessage;
import com.aizihe.codeaai.ai.model.message.ToolRequestMessage;
import com.aizihe.codeaai.core.parser.CodeParserExecutor;
import com.aizihe.codeaai.core.save.CodeSaveExecutor;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.core.parser.CodeParser;
import com.aizihe.codeaai.core.parser.ParseHtmlCode;
import com.aizihe.codeaai.core.parser.ParseMultiFileCode;
import com.aizihe.codeaai.ai.service.AiCodeGeneratorService;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import dev.dev.langchain4j.service.TokenStream;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.tool.ToolExecution;
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

    AiCodeGeneratorService aiCodeGeneratorService;

    @Resource
    AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    public static final CodeParser<SingleFileGenerateResult> singleCode = new ParseHtmlCode();
    public static final CodeParser<MultiFileGenerateResult> multiFileCode = new ParseMultiFileCode();

    /**
     * 通用流式生成 + 实时收集 + 结束后自动解析保存文件
     * @param userMessage 用户消息
     * @param appId 应用ID
     * @param codeGenTypeEnum 生成类型（区分HTML/多文件）
     * @return Flux<String> 流式响应
     */
    public Flux<String> generateAndSaveCodeStream(
            String userMessage,
            Long appId,
            CodeGenTypeEnum codeGenTypeEnum
    ) {
        // 1. 获取AI服务（带上类型，确保缓存正确）
        AiCodeGeneratorService aiCodeGeneratorService =
                aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);

        // 2. 根据类型调用不同的生成方法
        Flux<String> resultFlux;
        switch (codeGenTypeEnum) {
            case GEN_TYPE_HTML -> resultFlux = aiCodeGeneratorService.generateSignalCode(userMessage);
            case GEN_MULTI_FILE -> resultFlux = aiCodeGeneratorService.generateMultiCode(userMessage);
            case VUE_PROJECT ->resultFlux = aiCodeGeneratorService.generateVueProjectCodeStream(appId,userMessage);
            default -> throw new IllegalArgumentException("不支持的生成类型");
        }

        // 3. 统一流式收集 + 保存
        StringBuilder stringBuilder = new StringBuilder();

        return resultFlux
                .doOnNext(chunk -> {
                    if (chunk != null) {
                        synchronized (stringBuilder) {
                            stringBuilder.append(chunk);
                        }
                    }
                })
                .doOnTerminate(() -> {
                    synchronized (stringBuilder) {
                        try {
                            String fullContent = stringBuilder.toString();
                            if (fullContent.isBlank()) {
                                System.out.println("警告：生成内容为空，不保存文件");
                                return;
                            }

                            // 统一解析 + 保存
                            Object parsedObj = CodeParserExecutor.parse(fullContent, codeGenTypeEnum);
                            File savedFile = CodeSaveExecutor.execute(parsedObj, codeGenTypeEnum, appId);
                            System.out.println("文件保存成功：" + savedFile.getAbsolutePath());

                        } catch (Exception e) {
                            System.out.println("文件保存失败：" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 同一生成入口：根据类型生成并保存代码
     *
     * @param message
     * @param codeGenTypeEnum
     * @param appId
     * @return
     */
    public Flux<String> generateCode(String message, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "生成类型不存在");
        return switch (codeGenTypeEnum) {
            case GEN_TYPE_HTML, GEN_MULTI_FILE,VUE_PROJECT->generateAndSaveCodeStream(message,appId,codeGenTypeEnum);
            default -> {
                String msg = "不支持生成" + codeGenTypeEnum.getMessage() + "文件";
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, msg);
            }
        };
    }


    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }


    private Flux<String> generateAndeSaveHtmlCodeStream(String userMessage, Long appId) {
        aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
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
                                Object object = CodeParserExecutor.parse(completeHtmlCode, CodeGenTypeEnum.GEN_TYPE_HTML);
                                File file = CodeSaveExecutor.execute(object, CodeGenTypeEnum.GEN_TYPE_HTML,appId);
                                System.out.println("保存成功,路径为:" + file.getAbsolutePath());
                            } else {
                                System.out.println("警告: HTML 代码内容为空，未保存文件");
                            }
                        } catch (Exception e) {
                            System.out.println("文件保存失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    private Flux<String> generateAndSaveMultiCodeStream(String userMessage, Long appId) {
        //工厂类中获取实例
        aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> result = aiCodeGeneratorService.generateMultiCode(userMessage);
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
                                Object object = CodeParserExecutor.parse(completeMultiCode, CodeGenTypeEnum.GEN_MULTI_FILE);
                                File file = CodeSaveExecutor.execute(object, CodeGenTypeEnum.GEN_MULTI_FILE,appId);
                                System.out.println("保存文件成功,保存路径为:" + file.getAbsolutePath());
                            } else {
                                System.out.println("警告: 多文件代码内容为空，未保存文件");
                            }
                        } catch (Exception e) {
                            System.out.println("保存文件失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }


}