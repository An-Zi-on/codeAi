package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.common.ByIdRequest;
import com.aizihe.codeaai.domain.entity.ChatHistory;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryAdminPageRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryQueryRequest;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.ChatHistoryService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import dev.langchain4j.agent.tool.P;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public BaseResponse<Page<ChatHistoryVO>> pageByApp(@RequestParam Long appId,
                                                       @RequestParam LocalDateTime lastCreateTime,
                                                       @RequestParam int pageSize,
                                                        HttpServletRequest request) {
        UserVO currentUser = userService.current(request);
        return ResultUtils.success(chatHistoryService.pageAppHistory(appId, lastCreateTime, pageSize, currentUser));
    }

    /**
     * 管理员分页查询所有对话历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 对话历史分页
     */
    @PostMapping("/admin/list/page/vo")
    @MustRole(needRole = "admin")
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = chatHistoryQueryRequest.getPageNum();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        // 查询数据
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }
    @PostMapping("/history/lastTime")
    public BaseResponse<LocalDateTime> getLastLocalDateTime(@RequestBody ByIdRequest byIdRequest){
        ThrowUtils.throwIf(byIdRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = byIdRequest.getId();
        ThrowUtils.throwIf(id == null ||id <0,ErrorCode.PARAMS_ERROR,"请求参数错误");
        ChatHistoryVO lastHistory = chatHistoryService.getLastHistory(byIdRequest);
        if (lastHistory == null){
            return  ResultUtils.success(LocalDateTime.now());
        }
        return ResultUtils.success(lastHistory.getCreateTime());
    }
}
