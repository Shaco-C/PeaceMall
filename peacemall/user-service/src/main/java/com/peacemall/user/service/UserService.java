package com.peacemall.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.UserDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.user.domain.dto.LoginFormDTO;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.domain.vo.UserInfoVO;
import com.peacemall.user.domain.vo.UserLoginVO;
import com.peacemall.user.enums.UserState;

import java.util.List;

public interface UserService extends IService<Users> {
    // 注册用户
    R<String> register(Users users);

    // 用户登录
    R<UserLoginVO> login(LoginFormDTO loginFormDTO);

    // 用户注销账号
    R<String> closeAccount(String password);

    // 删除用户
    R<String> deleteUsersByIds(List<Long> userIds);

    R<String> deleteUserWithClosedState();

    // 更新用户信息
    R<String> updateUserInfos(Users users);

    // 更新用户密码
    R<String> updatePassword(String oldPassword, String newPassword);

    // 更新用户手机
    R<String> updatePhoneNumber(String oldNumber, String newNumber);

    // 更新用户邮箱
    R<String> updateEmail(String oldEmail, String newEmail);

    //获取用户个人信息
    R<UserInfoVO> getUserInfo();

    //管理员查看各种状态的账号
    R<PageDTO<Users>> getUsersWithStatus(int page, int pageSize , UserState status);

    //管理员根据用户id来切换用户的角色
    void adminChangeUserRole(Long userId, UserRole userRole);

    PageDTO<UserDTO> findAllUsersWithPage(int page, int size);
}
