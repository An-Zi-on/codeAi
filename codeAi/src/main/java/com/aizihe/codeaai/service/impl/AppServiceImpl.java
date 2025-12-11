package com.aizihe.codeaai.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.ai.core.AiCodeGeneratorFacade;
import com.aizihe.codeaai.ai.model.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.common.DeleteRequest;
import com.aizihe.codeaai.domain.common.DeployConstant;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.request.app.AppAdminPageRequest;
import com.aizihe.codeaai.domain.request.app.AppAdminUpdateRequest;
import com.aizihe.codeaai.domain.request.app.AppCreateRequest;
import com.aizihe.codeaai.domain.request.app.AppFeaturedPageRequest;
import com.aizihe.codeaai.domain.request.app.AppMyPageRequest;
import com.aizihe.codeaai.domain.request.app.AppUpdateMyRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.enums.ChatMessageTypeEnum;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.mapper.AppMapper;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 应用 服务层实现。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService {
    private static final int MAX_PAGE_SIZE = 20;
    private static final Logger log = LoggerFactory.getLogger(AppServiceImpl.class);

    @Resource
    private UserService userService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Override
    public String deployApp(Long appId, UserVO loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = DeployConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. 复制文件到部署目录
        String deployDirPath = DeployConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 8. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 9. 返回可访问的 URL
        return String.format("%s/%s/", DeployConstant.CODE_DEPLOY_HOST, deployKey);
    }

    @Override
    @Transactional
    public Flux<ServerSentEvent<String>> chatToGenCode(Long appId, String message, UserVO loginUser) {
        ThrowUtils.throwIf(!StrUtil.isNotBlank(message),ErrorCode.NOT_FOUND_ERROR,"描述不能为空");
        ThrowUtils.throwIf(appId == null||appId<0,ErrorCode.PARAMS_ERROR);
        App appDo = this.getById(appId);
        ThrowUtils.throwIf(appDo == null,ErrorCode.NOT_FOUND_ERROR);
        //仅本人和管理员才能在自己项目进行创建
        boolean notOwner = !appDo.getUserId().equals(loginUser.getId());
        boolean notAdmin = !UserRole.ADMIN.getValue().equals(loginUser.getUserRole());
        ThrowUtils.throwIf(notOwner && notAdmin, ErrorCode.NO_AUTH_ERROR, "无权限生成代码");
        //获取生成的代码类型
        String codeGenType = appDo.getCodeGenType();
        ThrowUtils.throwIf(!StrUtil.isNotBlank(codeGenType),ErrorCode.NOT_FOUND_ERROR,"生成代码的类型不存在");
        //获取对应生成类型
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null,ErrorCode.PARAMS_ERROR,"生成代码的类型不存在");
        //存储用户的消息
        Long result = chatHistoryService.saveMessage(
                ChatHistoryMessageSaveRequest.builder()
                        .message(message)
                        .messageType(ChatMessageTypeEnum.USER.getValue())
                        .appId(appId).build(), loginUser
        );
        ThrowUtils.throwIf(result < 0, ErrorCode.SYSTEM_ERROR, "用户消息存储失败");
        Flux<String> stringFlux = aiCodeGeneratorFacade.generateCode(message, codeGenTypeEnum, appId);
        // 使用线程安全的 StringBuilder 收集完整内容
        StringBuilder aiMessage = new StringBuilder();
        // 使用 AtomicReference 确保线程安全
        java.util.concurrent.atomic.AtomicReference<String> completeMessage = new java.util.concurrent.atomic.AtomicReference<>();
        
        return stringFlux
                // 收集每个数据块
                .doOnNext(chunk -> {
                    if (chunk != null) {
                        synchronized (aiMessage) {
                            aiMessage.append(chunk);
                        }
                    }
                })
                // 转换为 SSE 事件
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of("d", chunk != null ? chunk : "");
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder().data(jsonData).build();
                })
                // 确保在流完成时保存完整消息
                .doOnTerminate(() -> {
                    // 无论是正常完成还是异常终止，都尝试保存已收集的内容
                    synchronized (aiMessage) {
                        String messageContent = aiMessage.toString();
                        if (StrUtil.isNotBlank(messageContent)) {
                            completeMessage.set(messageContent);
                            try {
                                Long resultAi = chatHistoryService.saveMessage(
                                        ChatHistoryMessageSaveRequest.builder()
                                                .message(messageContent)
                                                .messageType(ChatMessageTypeEnum.AI.getValue())
                                                .appId(appId).build(), loginUser
                                );
                                if (resultAi < 0) {
                                    log.error("保存 AI 消息失败, appId={}, userId={}, messageLength={}", 
                                            appId, loginUser.getId(), messageContent.length());
                                } else {
                                    log.debug("AI 消息保存成功, appId={}, userId={}, messageLength={}", 
                                            appId, loginUser.getId(), messageContent.length());
                                }
                            } catch (Exception e) {
                                log.error("保存 AI 消息异常, appId={}, userId={}, messageLength={}", 
                                        appId, loginUser.getId(), messageContent.length(), e);
                            }
                        } else {
                            log.warn("AI 消息内容为空, appId={}, userId={}", appId, loginUser.getId());
                        }
                    }
                })
                // 在流正常完成时发送 done 事件
                .concatWith(Mono.just(ServerSentEvent.<String>builder().event("done").data("").build()))
                // 错误处理：先确保已收集的数据被保存，再处理错误
                .onErrorResume(error -> {
                    // 先保存已收集的内容
                    synchronized (aiMessage) {
                        String messageContent = aiMessage.toString();
                        if (StrUtil.isNotBlank(messageContent)) {
                            try {
                                chatHistoryService.saveMessage(
                                        ChatHistoryMessageSaveRequest.builder()
                                                .message(messageContent)
                                                .messageType(ChatMessageTypeEnum.AI.getValue())
                                                .appId(appId).build(), loginUser
                                );
                                log.info("异常情况下保存部分 AI 消息, appId={}, userId={}, messageLength={}", 
                                        appId, loginUser.getId(), messageContent.length());
                            } catch (Exception e) {
                                log.error("异常情况下保存 AI 消息失败, appId={}, userId={}", 
                                        appId, loginUser.getId(), e);
                            }
                        }
                    }
                    
                    // 客户端主动断开或网络 IO 问题，直接忽略避免再次触发 async dispatch
                    if (error instanceof ClientAbortException || error instanceof java.io.IOException) {
                        log.warn("客户端断开 SSE 连接, appId={}, userId={}", appId, loginUser.getId());
                        return Flux.empty();
                    }
                    
                    // 其他错误，发送错误事件
                    log.error("生成代码流异常, appId={}, userId={}", appId, loginUser.getId(), error);
                    return Flux.just(
                            ServerSentEvent.<String>builder().event("error").data(error.getMessage()).build(),
                            ServerSentEvent.<String>builder().event("done").data("").build()
                    );
                })
                // 添加背压控制，确保数据不会丢失
                .onBackpressureBuffer(1000);

    }

    @Override
    public Long createApp(AppCreateRequest appCreateRequest, HttpServletRequest request) {
        AppCreateRequest safeRequest = requireNonNull(appCreateRequest, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current(request);
        //先提取初始化提示词的前12位作为应用的初始名称
        validateRequiredString(safeRequest.getInitPrompt(), "初始化提示词", 10, 5000);
        LocalDateTime now = LocalDateTime.now();
        App app = App.builder()
                .appName(safeRequest.getInitPrompt().trim().substring(0,12))
                .initPrompt(safeRequest.getInitPrompt().trim())
                .codeGenType(optionalTrim(safeRequest.getCodeGenType()))
                .priority(0)
                .userId(currentUser.getId())
                .editTime(now)
                .createTime(now)
                .updateTime(now)
                .build();
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建应用失败");
        return app.getId();
    }

    @Override
    public Boolean updateMyApp(AppUpdateMyRequest updateMyRequest,HttpServletRequest request) {
        AppUpdateMyRequest safeRequest = requireNonNull(updateMyRequest, ErrorCode.PARAMS_ERROR);
        Long appId = requireNonNull(safeRequest.getId(), ErrorCode.PARAMS_ERROR);
        validateRequiredString(safeRequest.getAppName(), "应用名称", 2, 30);
        UserVO currentUser = userService.current(request);
        App dbApp = requireNonNull(getById(appId), ErrorCode.NOT_FOUND_ERROR);
        //仅当前用户修改自己用户
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()), ErrorCode.NO_AUTH_ERROR);
        App app = new App();
        app.setId(dbApp.getId());
        app.setAppName(safeRequest.getAppName().trim());
        app.setCover(safeRequest.getCover());
        LocalDateTime now = LocalDateTime.now();
        app.setEditTime(now);
        app.setUpdateTime(now);
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteMyApp(DeleteRequest deleteRequest,HttpServletRequest request) {
        Long appId = deleteRequest.getId();
        Long safeAppId = requireNonNull(appId, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current(request);
        App dbApp = requireNonNull(getById(safeAppId), ErrorCode.NOT_FOUND_ERROR);
        //仅本人和管理员可删除
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()) && !UserRole.ADMIN.getValue().equals(currentUser.getUserRole())
                , ErrorCode.NO_AUTH_ERROR);
        boolean result = this.removeById(safeAppId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        //删除应用 也需要删除相关的应用关联的历史记录
        boolean chatResult = chatHistoryService.removeByAppId(safeAppId);
        ThrowUtils.throwIf(!chatResult, ErrorCode.SYSTEM_ERROR, "历史对话删除失败");
        return true;
    }

    @Override
    public App getMyAppDetail(Long appId,HttpServletRequest request) {
        Long safeAppId = requireNonNull(appId, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current(request);
        App dbApp = requireNonNull(getById(safeAppId), ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()), ErrorCode.NO_AUTH_ERROR);
        return dbApp;
    }

    @Override
    public Page<App> pageMyApps(AppMyPageRequest appMyPageRequest,HttpServletRequest request) {
        AppMyPageRequest safeRequest = requireNonNull(appMyPageRequest, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current(request);
        int current = normalizeCurrent(safeRequest.getPageNum());
        int pageSize = normalizeSize(safeRequest.getPageSize(), MAX_PAGE_SIZE);
        Page<App> page = Page.of(current, pageSize);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(App::getUserId, currentUser.getId())
                .eq(App::getIsDelete, 0)
                .orderBy(App::getUpdateTime, false);
        if (StrUtil.isNotBlank(safeRequest.getNameKeyword())) {
            queryWrapper.like(App::getAppName, safeRequest.getNameKeyword().trim());
        }
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<App> pageFeaturedApps(AppFeaturedPageRequest appFeaturedPageRequest,HttpServletRequest request) {
        AppFeaturedPageRequest safeRequest = requireNonNull(appFeaturedPageRequest, ErrorCode.PARAMS_ERROR);
        userService.current(request);
        int current = normalizeCurrent(safeRequest.getPageNum());
        int pageSize = normalizeSize(safeRequest.getPageSize(), MAX_PAGE_SIZE);
        Page<App> page = Page.of(current, pageSize);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(App::getIsDelete, 0)
                .ge(App::getPriority, 1)
                .orderBy(App::getPriority, false)
                .orderBy(App::getUpdateTime, false);
        if (StrUtil.isNotBlank(safeRequest.getNameKeyword())) {
            queryWrapper.like(App::getAppName, safeRequest.getNameKeyword().trim());
        }
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public Boolean adminDeleteApp(DeleteRequest request) {
        Long appId = request.getId();
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.removeById(appId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        //删除应用 也需要删除相关的应用关联的历史记录
        boolean chatResult = chatHistoryService.removeByAppId(appId);
        ThrowUtils.throwIf(!chatResult, ErrorCode.SYSTEM_ERROR, "历史对话删除失败");
        return true;
    }

    @Override
    public Boolean adminUpdateApp(AppAdminUpdateRequest request) {
        AppAdminUpdateRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        Long appId = requireNonNull(safeRequest.getId(), ErrorCode.PARAMS_ERROR);
        App dbApp = requireNonNull(getById(appId), ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        app.setId(dbApp.getId());
        if (StrUtil.isNotBlank(safeRequest.getAppName())) {
            validateRequiredString(safeRequest.getAppName(), "应用名称", 2, 30);
            app.setAppName(safeRequest.getAppName().trim());
        }
        if (StrUtil.isNotBlank(safeRequest.getCover())) {
            app.setCover(safeRequest.getCover().trim());
        }
        if (safeRequest.getPriority() != null) {
            ThrowUtils.throwIf(safeRequest.getPriority() < 0, ErrorCode.PARAMS_ERROR, "优先级必须为非负数");
            app.setPriority(safeRequest.getPriority());
        }
        ThrowUtils.throwIf(app.getAppName() == null && app.getCover() == null && app.getPriority() == null,
                ErrorCode.PARAMS_ERROR, "至少要修改一个字段");
        LocalDateTime now = LocalDateTime.now();
        app.setEditTime(now);
        app.setUpdateTime(now);
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }

    @Override
    public Page<App> adminPage(AppAdminPageRequest request) {
        AppAdminPageRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        int current =request.getPageNum();
        int size = safeRequest.getPageSize();
        Page<App> page = Page.of(current, size);
        // 动态拼接参数
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (safeRequest.getId() != null) {
            queryWrapper.eq(App::getId, safeRequest.getId());
        }
        if (StrUtil.isNotBlank(safeRequest.getAppName())) {
            queryWrapper.like(App::getAppName, safeRequest.getAppName().trim());
        }
        if (StrUtil.isNotBlank(safeRequest.getCover())) {
            queryWrapper.like(App::getCover, safeRequest.getCover().trim());
        }
        if (StrUtil.isNotBlank(safeRequest.getInitPrompt())) {
            queryWrapper.like(App::getInitPrompt, safeRequest.getInitPrompt().trim());
        }
        if (StrUtil.isNotBlank(safeRequest.getCodeGenType())) {
            queryWrapper.eq(App::getCodeGenType, safeRequest.getCodeGenType().trim());
        }
        if (StrUtil.isNotBlank(safeRequest.getDeployKey())) {
            queryWrapper.eq(App::getDeployKey, safeRequest.getDeployKey().trim());
        }
        if (safeRequest.getPriority() != null) {
            queryWrapper.eq(App::getPriority, safeRequest.getPriority());
        }
        if (safeRequest.getUserId() != null) {
            queryWrapper.eq(App::getUserId, safeRequest.getUserId());
        }
        if (safeRequest.getIsDelete() != null) {
            queryWrapper.eq(App::getIsDelete, safeRequest.getIsDelete());
        }
        queryWrapper.orderBy(App::getUpdateTime, false);
        return this.page(page, queryWrapper);
    }

    @Override
    public App adminGetDetail(Long id) {
        Long safeId = requireNonNull(id, ErrorCode.PARAMS_ERROR);
        App byId = getById(safeId);
        return requireNonNull(byId, ErrorCode.NOT_FOUND_ERROR);
    }

    /**
     *
     * @param value
     * @param fieldName
     * @param minLength
     * @param maxLength
     */
    private void validateRequiredString(String value, String fieldName, int minLength, int maxLength) {
        ThrowUtils.throwIf(StrUtil.isBlank(value), ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
        String trimmed = value.trim();
        ThrowUtils.throwIf(trimmed.length() < minLength || trimmed.length() > maxLength,
                ErrorCode.PARAMS_ERROR,
                String.format("%s长度需在%d-%d之间", fieldName, minLength, maxLength));
    }

    private int normalizeCurrent(Integer current) {
        return (current == null || current <= 0) ? 1 : current;
    }

    private int normalizeSize(Integer size, int maxSize) {
        int realSize = (size == null || size <= 0) ? 10 : size;
        return Math.min(realSize, maxSize);
    }

    private String optionalTrim(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }

    /**
     * 请求参数校验
     * @param value 参数值
     * @param errorCode 错误码
     * @return
     * @param <T>
     */
    private <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            ThrowUtils.throwIf(true, errorCode);
        }
        return value;
    }
}
