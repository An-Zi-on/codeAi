package com.aizihe.codeaai.service;

import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.common.DeleteRequest;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.request.app.AppAdminPageRequest;
import com.aizihe.codeaai.domain.request.app.AppAdminUpdateRequest;
import com.aizihe.codeaai.domain.request.app.AppCreateRequest;
import com.aizihe.codeaai.domain.request.app.AppFeaturedPageRequest;
import com.aizihe.codeaai.domain.request.app.AppMyPageRequest;
import com.aizihe.codeaai.domain.request.app.AppUpdateMyRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * 应用 服务层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
public interface AppService extends IService<App> {
   Flux<ServerSentEvent<String>> chatToGenCode(Long appId, String message, UserVO loginUser);

    /**
     * 用户创建应用
     */
    Long createApp(AppCreateRequest request);

    /**
     * 用户更新自己的应用（目前仅支持名称）
     */
    Boolean updateMyApp(AppUpdateMyRequest request);

    /**
     * 用户删除自己的应用
     */
    Boolean deleteMyApp(DeleteRequest request);

    /**
     * 用户查看自己应用详情
     */
    App getMyAppDetail(Long appId);

    /**
     * 用户分页查询自己的应用（最多 20 条）
     */
    Page<App> pageMyApps(AppMyPageRequest request);

    /**
     * 分页查询精选应用（最多 20 条）
     */
    Page<App> pageFeaturedApps(AppFeaturedPageRequest request);

    /**
     * 管理员删除任意应用
     */
    Boolean adminDeleteApp(DeleteRequest request);

    /**
     * 管理员更新任意应用名称 / 封面 / 优先级
     */
    Boolean adminUpdateApp(AppAdminUpdateRequest request);

    /**
     * 管理员分页查询应用（条件不限字段）
     */
    Page<App> adminPage(AppAdminPageRequest request);

    /**
     * 管理员查看应用详情
     */
    App adminGetDetail(Long id);
}
