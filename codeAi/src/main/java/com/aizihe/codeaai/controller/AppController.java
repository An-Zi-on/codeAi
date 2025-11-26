package com.aizihe.codeaai.controller;

import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.AppVO;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.common.DeleteRequest;
import com.aizihe.codeaai.domain.entity.App;
import com.aizihe.codeaai.domain.request.app.AppAdminPageRequest;
import com.aizihe.codeaai.domain.request.app.AppAdminUpdateRequest;
import com.aizihe.codeaai.domain.request.app.AppCreateRequest;
import com.aizihe.codeaai.domain.request.app.AppFeaturedPageRequest;
import com.aizihe.codeaai.domain.request.app.AppMyPageRequest;
import com.aizihe.codeaai.domain.request.app.AppUpdateMyRequest;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 应用 控制层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;
    @Resource
    private UserService userService;
    // ========== 用户侧 ==========
    /**
     * 用户生成代码
     */
    @GetMapping(value="/chat/gen/code",produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generateCode(@RequestParam Long appId,
                                                      @RequestParam String message
                                          ) {
        ThrowUtils.throwIf(appId == null ||appId<0, ErrorCode.PARAMS_ERROR,"请求参数错误");
        ThrowUtils.throwIf(!StrUtil.isNotBlank(message),ErrorCode.PARAMS_ERROR,"信息为空");
        UserVO current = userService.current();
       return appService.chatToGenCode(appId, message, current);
    }

    /**
     * 用户创建应用
     */
    @PostMapping("/my/create")
    public BaseResponse<Long> createApp(@RequestBody AppCreateRequest request) {
        return ResultUtils.success(appService.createApp(request));
    }

    /**
     * 用户更新自己的应用
     */
    @PutMapping("/my/update")
    public BaseResponse<Boolean> updateMyApp(@RequestBody AppUpdateMyRequest request) {
        return ResultUtils.success(appService.updateMyApp(request));
    }

    /**
     * 用户删除自己的应用
     */
    @PostMapping("/my/delete")
    public BaseResponse<Boolean> deleteMyApp(@RequestBody DeleteRequest request) {
        return ResultUtils.success(appService.deleteMyApp(request));
    }

    /**
     * 用户查询自己的应用详情
     */
    @GetMapping("/my/{id}")
    public BaseResponse<AppVO> getMyApp(@PathVariable Long id) {
        return ResultUtils.success(AppVO.toVo(appService.getMyAppDetail(id)));
    }

    /**
     * 用户分页查询自己的应用（最多 20 条）
     */
    @PostMapping("/my/page")
    public BaseResponse<Page<AppVO>> pageMyApps(@RequestBody AppMyPageRequest request) {
        return ResultUtils.success(convertPage(appService.pageMyApps(request)));
    }

    /**
     * 用户分页查询精选应用（最多 20 条）
     */
    @PostMapping("/featured/page")
    public BaseResponse<Page<AppVO>> pageFeaturedApps(@RequestBody AppFeaturedPageRequest request) {
        return ResultUtils.success(convertPage(appService.pageFeaturedApps(request)));
    }

    // ========== 管理员 ==========

    /**
     * 管理员删除任意应用
     */
    @PostMapping("/admin/delete")
    @MustRole(needRole = "admin")
    public BaseResponse<Boolean> adminDelete(@RequestBody DeleteRequest request) {
        return ResultUtils.success(appService.adminDeleteApp(request));
    }

    /**
     * 管理员更新应用
     */
    @PutMapping("/admin/update")
    @MustRole(needRole = "admin")
    public BaseResponse<Boolean> adminUpdate(@RequestBody AppAdminUpdateRequest request) {
        return ResultUtils.success(appService.adminUpdateApp(request));
    }

    /**
     * 管理员分页查询应用（字段任意组合，数量不限）
     */
    @PostMapping("/admin/page")
    @MustRole(needRole = "admin")
    public BaseResponse<Page<AppVO>> adminPage(@RequestBody AppAdminPageRequest request) {
        return ResultUtils.success(convertPage(appService.adminPage(request)));
    }

    /**
     * 管理员查看应用详情
     */
    @GetMapping("/admin/{id}")
    @MustRole(needRole = "admin")
    public BaseResponse<AppVO> adminGetDetail(@PathVariable Long id) {
        return ResultUtils.success(AppVO.toVo(appService.adminGetDetail(id)));
    }

    private Page<AppVO> convertPage(Page<App> source) {
        if (source == null) {
            return null;
        }
        Page<AppVO> target = new Page<>();
        target.setPageNumber(source.getPageNumber());
        target.setPageSize(source.getPageSize());
        target.setTotalRow(source.getTotalRow());
        target.setTotalPage(source.getTotalPage());

        List<AppVO> voRecords = source.getRecords() == null
                ? Collections.emptyList()
                : source.getRecords().stream()
                .map(AppVO::toVo)
                .collect(Collectors.toList());
        target.setRecords(voRecords);
        return target;
    }
}
