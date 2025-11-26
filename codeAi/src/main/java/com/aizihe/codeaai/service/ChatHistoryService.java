package com.aizihe.codeaai.service;

import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.entity.ChatHistory;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryAdminPageRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryPageRequest;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 对话历史 服务层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 保存对话消息
     */
    Long saveMessage(ChatHistoryMessageSaveRequest request, UserVO currentUser);

    /**
     * 用户/应用侧分页查询
     */
    Page<ChatHistoryVO> pageAppHistory(ChatHistoryPageRequest request, UserVO currentUser);

    /**
     * 管理员分页查询
     */
    Page<ChatHistoryVO> pageAdminHistory(ChatHistoryAdminPageRequest request);

    /**
     * 根据应用删除历史
     */
    boolean removeByAppId(Long appId);
}
