package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.domain.request.user.UserLoginRequest;
import com.aizihe.codeaai.domain.request.user.UserRegisterRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdatePwdRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdateRequest;
import com.aizihe.codeaai.exception.ErrorCode;
import com.aizihe.codeaai.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aizihe.codeaai.domain.common.Constants.USER_CACHE;

/**
 * 用户 控制层。
 *
 * @author An
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    /**
     * 保存用户。
     *
     * @param user 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     * 仅管理员使用
     */
    @PostMapping("save")
    @MustRole(needRole = "admin")
    public BaseResponse<Boolean> save(@RequestBody User user) {
        return  ResultUtils.success(userService.save(user));
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     * 根据主键更新用户。
     */
    @DeleteMapping("remove/{id}")
    @MustRole(needRole = "admin")
    public BaseResponse<Boolean> remove(@PathVariable Long id) {
        return ResultUtils.success(userService.removeById(id));
    }

    /**
     * 根据主键更新用户。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     * 根据主键更新用户。
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody UserUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.checkUpdate(request);
        return ResultUtils.success(userService.updateById(user));
    }
    /**
     * 根据主键更新用户密码。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     * 根据主键更新用户。
     */
    @PutMapping("/update/pwd")
    public BaseResponse<Boolean> updatePwd(@RequestBody UserUpdatePwdRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.checkPssword(request);
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     * 仅管理员使用
     */
    @GetMapping("list")
    public BaseResponse<List<UserVO>> list() {
        List<User> users = userService.list();
        if (users == null || users.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }
        return ResultUtils.success(
                users.stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList()));

    }

    /**
     * 获取当前登入的用户
     *
     * @param
     * @return 用户详情
     */
    @GetMapping("/current")
    public BaseResponse<UserVO> getInfo() {
        return ResultUtils.success(userService.current());
    }

    /**
     * 分页查询用户。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public BaseResponse<Page<UserVO>> page(Page<User> page) {
        Page<User> entityPage = userService.page(page);
        return ResultUtils.success(convertPage(entityPage));
    }

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Boolean> register(@RequestBody  UserRegisterRequest request){
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.userRegister(request));
    }
    /**
     * 用户登入
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<Boolean> login(@RequestBody UserLoginRequest request , HttpServletRequest httpServletRequest){
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.userLogin(request,httpServletRequest));
    }

    /**
     * 用户登入
     * @return
     */
    @PostMapping("/loginOut")
    public BaseResponse<String> loginOut(HttpServletRequest request){
       request.removeAttribute(USER_CACHE);
       return ResultUtils.success("OK");
    }

    private Page<UserVO> convertPage(Page<User> source) {
        if (source == null) {
            return null;
        }
        Page<UserVO> target = new Page<>();
        target.setPageNumber(source.getPageNumber());
        target.setPageSize(source.getPageSize());
        target.setTotalRow(source.getTotalRow());
        target.setTotalPage(source.getTotalPage());

        List<UserVO> records = source.getRecords() == null
                ? Collections.emptyList()
                : source.getRecords().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList());
        target.setRecords(records);
        return target;
    }
}
