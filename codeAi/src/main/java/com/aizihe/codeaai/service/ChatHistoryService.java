package com.aizihe.codeaai.service;

import com.aizihe.codeaai.domain.VO.ChatHistoryVO;
import com.aizihe.codeaai.domain.common.ByIdRequest;
import com.aizihe.codeaai.domain.entity.ChatHistory;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryMessageSaveRequest;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.request.chathistory.ChatHistoryQueryRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

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
    Page<ChatHistoryVO> pageAppHistory(Long appId, LocalDateTime lastCreateTime, int pageSize, UserVO currentUser);

    /**
     * 拼接查询条件
     * @param request
     * @return
     */
     QueryWrapper getQueryWrapper(ChatHistoryQueryRequest request);
    /**
     * 根据应用删除历史
     */
    boolean removeByAppId(Long appId);

    /**
     * 加载历史消息
     * @param appId
     * @param chatMemory
     * @param maxCount
     * @return
     */
     int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    ChatHistoryVO getLastHistory(ByIdRequest byIdRequest);
}
