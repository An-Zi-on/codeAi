package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.request.app.AppDeployRequest;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.AppService;
import com.aizihe.codeaai.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 应用 控制层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@RestController
@RequestMapping("/deploy")
public class DeployController{
    @Resource
    private UserService userService;
    @Resource
    private AppService appService;
    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest) {
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 获取当前登录用户
        UserVO loginUser = userService.current();
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deployUrl);
    }

//
//    /**
//     * 用户创建应用
//     */
//    @PostMapping("/start")
//    public BaseResponse<String> startApp(@RequestBody GetRequest request) {
//        ThrowUtils.throwIf(request == null,ErrorCode.NOT_FOUND_ERROR);
//        return ResultUtils.success(nodeService.developMyApp(request.getId()));
//    }
//
//    /**
//     * 用户创建应用
//     */
//    @PostMapping("/stop")
//    public BaseResponse<String> stopApp(@RequestBody DeleteRequest request) {
//        ThrowUtils.throwIf(request == null,ErrorCode.NOT_FOUND_ERROR);
//        return ResultUtils.success(nodeService.stopMyApp(request.getId()));
//    }

}
