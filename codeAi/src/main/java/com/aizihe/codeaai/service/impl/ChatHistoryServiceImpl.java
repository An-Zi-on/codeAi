package com.aizihe.codeaai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.entity.ChatHistory;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryAdminPageRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryQueryRequest;
import com.aizihe.codeaai.enums.ChatMessageTypeEnum;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.mapper.ChatHistoryMapper;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private AppService appService;

    @Resource
    private UserService userService;

    @Override
    public Long saveMessage(ChatHistoryMessageSaveRequest request, UserVO currentUser) {
        //参数校验
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
    public Page<ChatHistoryVO> pageAppHistory(Long appId, LocalDateTime lastCreateTime, int pageSize, UserVO currentUser) {
        //校验页数是否正确
        ThrowUtils.throwIf(pageSize < 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "请求页数不正确");
        //校验用户是否登入
        UserVO safeUser = requireNonNull(currentUser, ErrorCode.NOT_LOGIN_ERROR);
        //校验appId是否正确
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不正确");
        //校验是否有权限
        ensureAppAccessible(appId, safeUser);
        //构建查询参数
        ChatHistoryQueryRequest request = new ChatHistoryQueryRequest();
        request.setAppId(appId);
        request.setUserId(safeUser.getId());
        request.setPageSize(pageSize);
        request.setLastCreateTime(lastCreateTime);
        //限制查询
        Page<ChatHistory> resultDO = this.page(new Page<>(request.getPageNum(), request.getPageSize()), getQueryWrapper(request));
        return convertPage(resultDO);
    }

    /**
     * 构建查询条件
     *
     * @param request
     * @return
     */
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest request) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (request == null) {
            return queryWrapper;
        }
        Long id = request.getId();
        String message = request.getMessage();
        String messageType = request.getMessageType();
        Long appId = request.getAppId();
        Long userId = request.getUserId();
        LocalDateTime lastCreateTime = request.getLastCreateTime();
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        queryWrapper.eq(ChatHistory::getId, id);
        queryWrapper.like(ChatHistory::getMessage, message);
        queryWrapper.eq(ChatHistory::getMessageType, messageType);
        queryWrapper.eq(ChatHistory::getAppId, appId);
        queryWrapper.eq(ChatHistory::getUserId, userId);
        if (lastCreateTime != null) {
            //游标排序,只取查询当前的时间为条件
            queryWrapper.le(ChatHistory::getCreateTime, lastCreateTime);
        }
        //排序规则
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            //按照时间排序,降序,时间越靠近现在越在前面.取最靠近的的数据
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
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

    /**
     * 校验app是是否可用
     *
     * @param appId
     * @param currentUser
     * @return
     */
    private App ensureAppAccessible(Long appId, UserVO currentUser) {
        App app = appService.getById(appId);
        //校验app是否存在
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //仅本人和管理员可用
        boolean isOwner = app.getUserId().equals(currentUser.getId());
        boolean isAdmin = UserRole.ADMIN.getValue().equals(currentUser.getUserRole());
        ThrowUtils.throwIf(!isOwner && !isAdmin, ErrorCode.NO_AUTH_ERROR);
        return app;
    }

    private Page<ChatHistory> pageWithWrapper(Integer pageNumber, Integer pageSize, QueryWrapper queryWrapper) {
        int currentNum = normalizeCurrent(pageNumber);
        int currentSize = normalizeSize(pageSize);
        Page<ChatHistory> page = Page.of(currentNum, currentSize);
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
                .map(ChatHistoryVO::toVO)
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

    private <T> T requireNonNull(T value, ErrorCode errorCode, String message) {
        if (value == null) {
            ThrowUtils.throwIf(true, errorCode, message);
        }
        return value;
    }
}
