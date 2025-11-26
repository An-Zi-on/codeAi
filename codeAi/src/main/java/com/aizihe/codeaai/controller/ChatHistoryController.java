package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryAdminPageRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryPageRequest;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对话历史 控制层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@RestController
@RequestMapping("/chat/history")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private UserService userService;

    /**
     * 保存一条对话消息
     */
    @PostMapping("/message")
    public BaseResponse<Long> saveMessage(@RequestBody ChatHistoryMessageSaveRequest request,
                                          HttpServletRequest httpServletRequest) {
        UserVO currentUser = userService.current(httpServletRequest);
        return ResultUtils.success(chatHistoryService.saveMessage(request, currentUser));
    }

    /**
     * 应用页分页查询（最新 10 条，支持翻页）
     */
    @PostMapping("/page")
    public BaseResponse<Page<ChatHistoryVO>> pageByApp(@RequestBody ChatHistoryPageRequest request,
                                                       HttpServletRequest httpServletRequest) {
        UserVO currentUser = userService.current(httpServletRequest);
        return ResultUtils.success(chatHistoryService.pageAppHistory(request, currentUser));
    }

    /**
     * 管理员分页查询所有应用对话历史（时间倒序）
     */
    @PostMapping("/admin/page")
    @MustRole(needRole = "admin")
    public BaseResponse<Page<ChatHistoryVO>> adminPage(@RequestBody ChatHistoryAdminPageRequest request) {
        return ResultUtils.success(chatHistoryService.pageAdminHistory(request));
    }
}
