package com.aizihe.codeaai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.entity.ChatHistory;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryAdminPageRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryPageRequest;
import com.aizihe.codeaai.enums.ChatMessageTypeEnum;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.mapper.ChatHistoryMapper;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话历史 服务层实现。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    private static final int MAX_PAGE_SIZE = 20;

    @Resource
    private AppService appService;

    @Override
    public Long saveMessage(ChatHistoryMessageSaveRequest request, UserVO currentUser) {
        ChatHistoryMessageSaveRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        UserVO safeUser = requireNonNull(currentUser, ErrorCode.NOT_LOGIN_ERROR);
        Long appId = safeRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不正确");
        String message = safeRequest.getMessage();
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ChatMessageTypeEnum messageTypeEnum = requireNonNull(
                ChatMessageTypeEnum.fromValue(safeRequest.getMessageType()), ErrorCode.PARAMS_ERROR);
        App app = ensureAppAccessible(appId, safeUser);

        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(safeUser.getId())
                .message(message.trim())
                .messageType(messageTypeEnum.getValue())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDelete(0)
                .build();
        boolean result = this.save(chatHistory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存消息失败");
        // 更新应用编辑时间，方便排序
        App updateApp = new App();
        updateApp.setId(app.getId());
        updateApp.setEditTime(LocalDateTime.now());
        updateApp.setUpdateTime(LocalDateTime.now());
        appService.updateById(updateApp);
        return chatHistory.getId();
    }

    @Override
    public Page<ChatHistoryVO> pageAppHistory(ChatHistoryPageRequest request, UserVO currentUser) {
        ChatHistoryPageRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        UserVO safeUser = requireNonNull(currentUser, ErrorCode.NOT_LOGIN_ERROR);
        Long appId = safeRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不正确");
        ensureAppAccessible(appId, safeUser);
        Page<ChatHistory> entityPage = pageWithWrapper(safeRequest.getCurrent(), safeRequest.getSize(),
                QueryWrapper.create()
                        .eq(ChatHistory::getAppId, appId)
                        .eq(ChatHistory::getIsDelete, 0)
                        .orderBy(ChatHistory::getCreateTime, false));
        return convertPage(entityPage);
    }

    @Override
    public Page<ChatHistoryVO> pageAdminHistory(ChatHistoryAdminPageRequest request) {
        ChatHistoryAdminPageRequest safeRequest = requireNonNull(request, ErrorCode.PARAMS_ERROR);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(ChatHistory::getIsDelete, 0)
                .orderBy(ChatHistory::getCreateTime, false);
        if (safeRequest.getAppId() != null) {
            queryWrapper.eq(ChatHistory::getAppId, safeRequest.getAppId());
        }
        if (safeRequest.getUserId() != null) {
            queryWrapper.eq(ChatHistory::getUserId, safeRequest.getUserId());
        }
        if (StrUtil.isNotBlank(safeRequest.getMessageType())) {
            queryWrapper.eq(ChatHistory::getMessageType, safeRequest.getMessageType().trim());
        }
        Page<ChatHistory> entityPage = pageWithWrapper(safeRequest.getCurrent(), safeRequest.getSize(), queryWrapper);
        return convertPage(entityPage);
    }

    @Override
    public boolean removeByAppId(Long appId) {
        if (appId == null || appId <= 0) {
            return false;
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(ChatHistory::getAppId, appId);
        return this.remove(queryWrapper);
    }

    private App ensureAppAccessible(Long appId, UserVO currentUser) {
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isOwner = app.getUserId().equals(currentUser.getId());
        boolean isAdmin = UserRole.ADMIN.getValue().equals(currentUser.getUserRole());
        ThrowUtils.throwIf(!isOwner && !isAdmin, ErrorCode.NO_AUTH_ERROR);
        return app;
    }

    private Page<ChatHistory> pageWithWrapper(Integer current, Integer size, QueryWrapper queryWrapper) {
        int pageNumber = normalizeCurrent(current);
        int pageSize = normalizeSize(size);
        Page<ChatHistory> page = Page.of(pageNumber, pageSize);
        return this.page(page, queryWrapper);
    }

    private Page<ChatHistoryVO> convertPage(Page<ChatHistory> source) {
        if (source == null) {
            return null;
        }
        Page<ChatHistoryVO> target = new Page<>();
        target.setPageNumber(source.getPageNumber());
        target.setPageSize(source.getPageSize());
        target.setTotalRow(source.getTotalRow());
        target.setTotalPage(source.getTotalPage());
        List<ChatHistoryVO> records = source.getRecords() == null
                ? Collections.emptyList()
                : source.getRecords().stream()
                .map(ChatHistoryVO::fromEntity)
                .collect(Collectors.toList());
        target.setRecords(records);
        return target;
    }

    private int normalizeCurrent(Integer current) {
        return (current == null || current <= 0) ? 1 : current;
    }

    private int normalizeSize(Integer size) {
        int realSize = (size == null || size <= 0) ? 10 : size;
        return Math.min(realSize, MAX_PAGE_SIZE);
    }

    private <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            ThrowUtils.throwIf(true, errorCode);
        }
        return value;
    }
}
