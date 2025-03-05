package com.peacemall.user.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.user.domain.dto.LoginFormDTO;
import com.peacemall.user.domain.dto.VerifyPwdDTO;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.domain.vo.UserInfoVO;
import com.peacemall.user.domain.vo.UserLoginVO;
import com.peacemall.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户服务相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    // 用户注册
    @PostMapping("/register")
    public R<String> register(Users users) {
        return userService.register(users);
    }

    // 用户登录
    @PostMapping("/login")
    public R<UserLoginVO> login(LoginFormDTO loginFormDTO) {
        return userService.login(loginFormDTO);
    }

    // 用户注销
    @PutMapping("/closeAccount")
    public R<String> closeAccount(@RequestBody VerifyPwdDTO verifyPwdDTO) {
        return userService.closeAccount(verifyPwdDTO.getCurrentPassword());
    }

    //更新用户信息
    @PutMapping("/updateUserInfo")
    public R<String> updateUserInfo(@RequestBody Users users) {
        return userService.updateUserInfos(users);
    }

    //用户修改密码
    @PutMapping("/updatePassword")
    public R<String> updatePassword(@RequestBody VerifyPwdDTO verifyPwdDTO) {
        return userService.updatePassword(verifyPwdDTO.getCurrentPassword(), verifyPwdDTO.getNewPassword());
    }

    //获取用户个人信息
    @GetMapping("/getUserInfo")
    public R<UserInfoVO> getUserInfo() {
        return userService.getUserInfo();
    }


    //删除用户
    @DeleteMapping("/admin/deleteUsers")
    public R<String> adminDeleteUsers(@RequestBody IdsDTO idsDTO) {
        return userService.deleteUsersByIds(idsDTO.getIds());
    }

    //管理员删除注销账号的用户
    @DeleteMapping("/admin/deleteCloseAccountUsers")
    public R<String> adminDeleteClosedAccountUsers() {
        return userService.deleteUserWithClosedState();
    }

}
