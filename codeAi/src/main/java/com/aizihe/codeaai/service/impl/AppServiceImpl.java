package com.aizihe.codeaai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.request.app.AppAdminPageRequest;
import com.aizihe.codeaai.domain.request.app.AppAdminUpdateRequest;
import com.aizihe.codeaai.domain.request.app.AppCreateRequest;
import com.aizihe.codeaai.domain.request.app.AppFeaturedPageRequest;
import com.aizihe.codeaai.domain.request.app.AppMyPageRequest;
import com.aizihe.codeaai.domain.request.app.AppUpdateMyRequest;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.mapper.AppMapper;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 应用 服务层实现。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService {

    private static final int MAX_PAGE_SIZE = 20;

    @Resource
    private UserService userService;

    @Override
    public Long createApp(AppCreateRequest request) {
        AppCreateRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current();
        validateRequiredString(safeRequest.getAppName(), "应用名称", 2, 30);
        validateRequiredString(safeRequest.getInitPrompt(), "初始化提示词", 10, 5000);
        LocalDateTime now = LocalDateTime.now();
        App app = App.builder()
                .appName(safeRequest.getAppName().trim())
                .cover(optionalTrim(safeRequest.getCover()))
                .initPrompt(safeRequest.getInitPrompt().trim())
                .codeGenType(optionalTrim(safeRequest.getCodeGenType()))
                .deployKey(optionalTrim(safeRequest.getDeployKey()))
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
    public Boolean updateMyApp(AppUpdateMyRequest request) {
        AppUpdateMyRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        Long appId = requireNonNull(safeRequest.getId(), ErrorCode.PARAMS_ERROR);
        validateRequiredString(safeRequest.getAppName(), "应用名称", 2, 30);
        UserVO currentUser = userService.current();
        App dbApp = requireNonNull(getById(appId), ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()), ErrorCode.NO_AUTH_ERROR);
        App app = new App();
        app.setId(dbApp.getId());
        app.setAppName(safeRequest.getAppName().trim());
        LocalDateTime now = LocalDateTime.now();
        app.setEditTime(now);
        app.setUpdateTime(now);
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }

    @Override
    public Boolean deleteMyApp(Long appId) {
        Long safeAppId = requireNonNull(appId, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current();
        App dbApp = requireNonNull(getById(safeAppId), ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()), ErrorCode.NO_AUTH_ERROR);
        boolean result = this.removeById(safeAppId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        return true;
    }

    @Override
    public App getMyAppDetail(Long appId) {
        Long safeAppId = requireNonNull(appId, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current();
        App dbApp = requireNonNull(getById(safeAppId), ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!dbApp.getUserId().equals(currentUser.getId()), ErrorCode.NO_AUTH_ERROR);
        return dbApp;
    }

    @Override
    public Page<App> pageMyApps(AppMyPageRequest request) {
        AppMyPageRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        UserVO currentUser = userService.current();
        int current = normalizeCurrent(safeRequest.getCurrent());
        int pageSize = normalizeSize(safeRequest.getSize(), MAX_PAGE_SIZE);
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
    public Page<App> pageFeaturedApps(AppFeaturedPageRequest request) {
        AppFeaturedPageRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        userService.current();
        int current = normalizeCurrent(safeRequest.getCurrent());
        int pageSize = normalizeSize(safeRequest.getSize(), MAX_PAGE_SIZE);
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
    public Boolean adminDeleteApp(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.removeById(appId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
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
        int current = normalizeCurrent(safeRequest.getCurrent());
        int size = (safeRequest.getSize() == null || safeRequest.getSize() <= 0) ? 10 : safeRequest.getSize();
        Page<App> page = Page.of(current, size);
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
        return requireNonNull(getById(safeId), ErrorCode.NOT_FOUND_ERROR);
    }

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

    private <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            ThrowUtils.throwIf(true, errorCode);
        }
        return value;
    }
}
