package com.peacemall.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.utils.UserContext;
import com.peacemall.user.domain.dto.LoginFormDTO;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.domain.vo.UserInfoVO;
import com.peacemall.user.domain.vo.UserLoginVO;
import com.peacemall.user.enums.UserRole;
import com.peacemall.user.enums.UserState;
import com.peacemall.user.mapper.UsersMapper;
import com.peacemall.user.service.UserService;

import com.peacemall.user.utils.PasswordUtil;
import com.peacemall.user.utils.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UserService {

    // 注册用户
    @Override
    public R<String> register(Users users) {

        if (users == null){
            log.error("用户信息为空{}",users);
            return R.error("用户信息为空,请重试");
        }
        if (StrUtil.isEmpty(users.getUsername()) || StrUtil.isEmpty(users.getPassword())){
            log.error("用户名或密码为空{}",users);
            return R.error("用户名或密码为空,请重试");
        }

        if (isUsernameExists(users.getUsername())){
            log.error("用户名已存在{}",users);
            return R.error("用户名已存在,请重试");
        }

        if (!PasswordValidator.isValid(users.getPassword())){
            log.error("密码不符合要求{}",users.getPassword());
            return R.error("密码必须至少包含一个字母，一个数字，长度在8到16位");
        }

        users.setStatus(UserState.ACTIVE);
        users.setRole(UserRole.USER);
        users.setPassword(PasswordUtil.passwordEncoder.encode(users.getPassword()));

        boolean res = save(users);
        if (!res){
            log.error("用户注册失败{}",users);
            return R.error("用户注册失败,请重试");
        }

        return R.ok("用户注册成功");
    }

    // TODO 用户登录
    @Override
    public R<UserLoginVO> login(LoginFormDTO loginFormDTO) {
        return null;
    }

    // 关闭账户
    //用户只能注销账户，不能直接删除账户
    @Override
    public R<String> closeAccount(String password) {
        Long userId = UserContext.getUserId();

        // 1. 检查是否登录
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        // 2. 查询用户信息
        Users users = this.getById(userId);
        if (users == null) {
            log.error("用户不存在: userId={}", userId);
            return R.error("用户不存在");
        }

        // 3. 校验密码
        if (!PasswordUtil.matches(password, users.getPassword())) {
            log.warn("用户输入了错误的密码: userId={}", userId);
            return R.error("密码错误");
        }

        // 4. 更新用户状态
        users.setStatus(UserState.CLOSED);
        boolean res = this.updateById(users);
        if (!res) {
            log.error("账户注销失败: userId={}", userId);
            return R.error("账户注销失败，请稍后重试");
        }

        log.info("用户成功注销账户: userId={}", userId);
        return R.ok("注销账户成功");
    }


    // 管理员删除用户
    @Override
    public R<String> deleteUsersByIds(List<Long> userIds) {
        String userRole = UserContext.getUserRole();
        Long currentUserId = UserContext.getUserId();
        log.info("useRole:{},userId:{}",userRole,currentUserId);
        log.info("userIds:{}",userIds);
        // 1. 权限检查
        if (!UserRole.ADMIN.name().equals(userRole)) {
            log.error("当前用户没有管理员权限: userId={}, userRole={}", currentUserId, userRole);
            return R.error("当前用户没有管理员权限");
        }

        // 2. 判断 userIds 是否为空
        if (CollectionUtil.isEmpty(userIds)) {
            log.warn("管理员删除用户时未提供 userIds: userId={}", currentUserId);
            return R.error("请选择要删除的用户");
        }

        // 3. 执行删除
        boolean res = this.removeByIds(userIds);
        if (!res) {
            log.error("删除用户失败: adminUserId={}, userIds={}", currentUserId, userIds);
            return R.error("删除用户失败，请稍后重试");
        }

        log.info("管理员成功删除用户: adminUserId={}, userIds={}", currentUserId, userIds);
        return R.ok("删除用户成功");
    }


    //由管理员定时删除注销的用户
    @Override
    public R<String> deleteUserWithClosedState() {
        String userRole = UserContext.getUserRole();
        Long adminUserId = UserContext.getUserId();

        log.info("userRole:{},userId:{}",userRole,adminUserId);

        // 1. 权限检查
        if (!UserRole.ADMIN.name().equals(userRole)) {
            log.error("当前用户没有管理员权限: userId={}, userRole={}", adminUserId, userRole);
            return R.error("当前用户没有管理员权限");
        }

        // 2. 删除所有状态为 CLOSED 的用户
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getStatus, UserState.CLOSED.name());

        int deletedCount = this.baseMapper.delete(queryWrapper);
        log.info("deleteUserWithClosedState: deletedCount={}", deletedCount);
        if (deletedCount == 0) {
            log.info("没有需要删除的用户: adminUserId={}", adminUserId);
            return R.ok("没有需要删除的用户");
        }

        log.info("管理员删除了 {} 个已注销的用户: adminUserId={}", deletedCount, adminUserId);
        return R.ok("成功删除 " + deletedCount + " 个用户");
    }


    // 更新用户信息
    @Override
    public R<String> updateUserInfos(Users users) {
        log.info("updateUserInfos: users={}", users);
        Long userId = UserContext.getUserId();

        // 1. 检查是否登录
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        // 2. 检查用户是否尝试修改受限字段
        if (users.getStatus() != null || users.getRole() != null ||
                StrUtil.isNotEmpty(users.getPhoneNumber()) || StrUtil.isNotEmpty(users.getEmail())) {
            log.error("用户尝试修改受限字段: userId={}, users={}", userId, users);
            return R.error("请勿在此修改手机号、邮箱或用户权限");
        }

        // 3. 只更新允许修改的字段
        Users updateUser = new Users();
        updateUser.setUserId(userId);

        if (StrUtil.isNotEmpty(users.getAvatarUrl())) {
            updateUser.setAvatarUrl(users.getAvatarUrl());
        }
        if (StrUtil.isNotEmpty(users.getSignature())) {
            updateUser.setSignature(users.getSignature());
        }
        if (StrUtil.isNotEmpty(users.getNickname())) {
            updateUser.setNickname(users.getNickname());
        }

        // 4. 使用 updateById 更新
        boolean res = this.updateById(updateUser);

        if (!res) {
            log.error("更新用户信息失败: userId={}", userId);
            return R.error("更新用户信息失败，请稍后重试");
        }

        log.info("用户成功更新信息: userId={}, 更新字段={}", userId, updateUser);
        return R.ok("更新用户信息成功");
    }



    @Override
    public R<String> updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();

        log.info("updatePassword: oldPassword={}, newPassword={}", oldPassword, newPassword);

        // 1. 检查用户是否已登录
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        // 2. 校验旧密码是否为空
        if (StrUtil.isEmpty(oldPassword)) {
            log.error("用户未提供旧密码: userId={}", userId);
            return R.error("请输入旧密码");
        }

        // 3. 校验新密码是否为空，是否符合安全要求
        if (StrUtil.isEmpty(newPassword) || !PasswordValidator.isValid(newPassword)) {
            log.error("新密码格式不符合要求: userId={}", userId);
            return R.error("密码必须至少包含一个字母，一个数字，长度在8到16位");
        }

        // 4. 查询用户信息
        Users user = this.getById(userId);
        if (user == null) {
            log.error("用户信息不存在: userId={}", userId);
            return R.error("用户信息异常，请重新登录");
        }

        // 5. 校验旧密码是否正确
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            log.error("密码验证失败: userId={}", userId);
            return R.error("旧密码错误");
        }

        // 6. 新密码不能与旧密码相同
        if (PasswordUtil.matches(newPassword, user.getPassword())) {
            log.error("新密码不能与旧密码相同: userId={}", userId);
            return R.error("新密码不能与旧密码相同");
        }

        // 7. 更新密码
        Users updateUser = new Users();
        updateUser.setUserId(userId);
        updateUser.setPassword(PasswordUtil.encryptPassword(newPassword));

        boolean res = this.updateById(updateUser);
        if (!res) {
            log.error("更新密码失败: userId={}", userId);
            return R.error("更新密码失败，请稍后重试");
        }

        log.info("用户密码更新成功: userId={}", userId);
        return R.ok("更新密码成功");
    }



    // 获取用户信息
    @Override
    public R<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        Users user = this.getById(userId);
        if (user == null) {
            log.error("用户信息不存在: userId={}", userId);
            return R.error("用户信息不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);
        return R.ok(userInfoVO);
    }

    //方法类

    public boolean isUsernameExists(String username) {
        return baseMapper.existsByUsername(username) > 0;
    }

    public boolean isEmailExists(String email) {
        return baseMapper.existsByEmail(email) > 0;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        return baseMapper.existsByPhoneNumber(phoneNumber) > 0;
    }


}