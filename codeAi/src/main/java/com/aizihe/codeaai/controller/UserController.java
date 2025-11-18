package com.aizihe.codeaai.controller;

import com.aizihe.codeaai.ThrowUtils.BaseResponse;
import com.aizihe.codeaai.ThrowUtils.ResultUtils;
import com.aizihe.codeaai.ThrowUtils.ThrowUtils;
import com.aizihe.codeaai.annotation.MustRole;
import com.aizihe.codeaai.domain.VO.UserVO;
import com.aizihe.codeaai.domain.request.user.UserLoginRequest;
import com.aizihe.codeaai.domain.request.user.UserRegisterRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdatePwdRequest;
import com.aizihe.codeaai.domain.request.user.UserUpdateRequest;
import com.aizihe.codeaai.enums.UserRole;
import com.aizihe.codeaai.exception.ErrorCode;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.aizihe.codeaai.domain.entity.User;
import com.aizihe.codeaai.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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
    public boolean save(@RequestBody User user) {
        return userService.save(user);
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
    public boolean remove(@PathVariable Long id) {
        return userService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     * 根据主键更新用户。
     */
    @PutMapping("/update")
    public boolean update(@RequestBody UserUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.checkUpdate(request);
        return userService.updateById(user);
    }
    /**
     * 根据主键更新用户密码。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     * 根据主键更新用户。
     */
    @PutMapping("/update/pwd")
    public boolean updatePwd(@RequestBody UserUpdatePwdRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.checkPssword(request);
        return userService.updateById(user);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     * 仅管理员使用
     */
    @GetMapping("list")
    public List<User> list() {
        return userService.list();
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
    public Page<User> page(Page<User> page) {
        return userService.page(page);
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
}
