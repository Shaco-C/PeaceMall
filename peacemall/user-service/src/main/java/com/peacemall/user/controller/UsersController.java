package com.peacemall.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.user.domain.dto.LoginFormDTO;
import com.peacemall.user.domain.dto.VerifyInfosDTO;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.domain.vo.UserInfoVO;
import com.peacemall.user.domain.vo.UserLoginVO;
import com.peacemall.user.enums.UserState;
import com.peacemall.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户服务相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    // 用户注册
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public R<String> register(@RequestBody Users users) {
        return userService.register(users);
    }

    // 用户登录
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody @Validated LoginFormDTO loginFormDTO) {
        return userService.login(loginFormDTO);
    }

    // 用户注销
    @ApiOperation(value = "用户注销")
    @PutMapping("/closeAccount")
    public R<String> closeAccount(@RequestBody VerifyInfosDTO verifyPwdDTO) {
        return userService.closeAccount(verifyPwdDTO.getCurrentInfo());
    }

    //更新用户信息
    @ApiOperation(value = "更新用户信息")
    @PutMapping("/updateUserInfo")
    public R<String> updateUserInfo(@RequestBody Users users) {
        return userService.updateUserInfos(users);
    }

    //用户修改密码
    @ApiOperation(value = "用户修改密码")
    @PutMapping("/updatePassword")
    public R<String> updatePassword(@RequestBody VerifyInfosDTO verifyPwdDTO) {
        return userService.updatePassword(verifyPwdDTO.getCurrentInfo(), verifyPwdDTO.getNewInfo());
    }

    //用户修改手机
    @ApiOperation(value = "用户修改手机")
    @PutMapping("/updatePhoneNumber")
    public R<String> updatePhoneNumber(@RequestBody VerifyInfosDTO verifyPwdDTO) {
        return userService.updatePhoneNumber(verifyPwdDTO.getCurrentInfo(), verifyPwdDTO.getNewInfo());
    }

    //用户修改邮箱
    @ApiOperation(value = "用户修改邮箱")
    @PutMapping("/updateEmail")
    public R<String> updateEmail(@RequestBody VerifyInfosDTO verifyPwdDTO) {
        return userService.updateEmail(verifyPwdDTO.getCurrentInfo(), verifyPwdDTO.getNewInfo());
    }

    //获取用户个人信息
    @ApiOperation(value = "获取用户个人信息")
    @GetMapping("/getUserInfo")
    public R<UserInfoVO> getUserInfo() {
        return userService.getUserInfo();
    }


    //删除用户
    @ApiOperation(value = "管理员批量删除用户")
    @DeleteMapping("/admin/deleteUsers")
    public R<String> adminDeleteUsers(@RequestBody IdsDTO idsDTO) {
        return userService.deleteUsersByIds(idsDTO.getIdsList());
    }

    //管理员删除注销账号的用户
    @ApiOperation(value = "管理员一键删除注销账号的用户")
    @DeleteMapping("/admin/deleteCloseAccountUsers")
    public R<String> adminDeleteClosedAccountUsers() {
        return userService.deleteUserWithClosedState();
    }

    //管理员根据用户状态查询用户
    @ApiOperation(value = "管理员根据用户状态查询用户")
    @GetMapping("/admin/getUsersByState")
    public R<Page<Users>> getUsersWithState(@RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "PageSize", defaultValue = "20") int pageSize,
                                            @RequestParam(value = "state", defaultValue = "ACTIVE") UserState status) {
        return userService.getUsersWithStatus(page,pageSize,status);
    }

    //管理员根据用户id来切换用户的角色
    @ApiOperation("管理员根据用户id来切换用户的角色")
    @PutMapping("/admin/changeUserRole")
    public void adminChangeUserRole(@RequestParam(value = "userId") Long userId,
                                    @RequestParam(value = "userRole") UserRole userRole){
        userService.adminChangeUserRole(userId,userRole);
    }

}
