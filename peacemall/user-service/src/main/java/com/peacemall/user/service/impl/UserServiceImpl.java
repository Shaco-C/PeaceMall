package com.peacemall.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.WalletClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.vo.WalletVO;
import com.peacemall.common.utils.UserContext;
import com.peacemall.user.domain.dto.LoginFormDTO;
import com.peacemall.user.domain.po.Users;
import com.peacemall.user.domain.vo.UserInfoVO;
import com.peacemall.user.domain.vo.UserLoginVO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.user.enums.UserState;
import com.peacemall.user.mapper.UsersMapper;
import com.peacemall.user.service.UserService;

import com.peacemall.user.utils.JwtUtils;
import com.peacemall.user.utils.PasswordUtil;
import com.peacemall.user.utils.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UserService {

    private final JwtUtils jwtUtils;
    private final WalletClient walletClient;

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
        //通过openfeign为用户创建钱包
        try{
            walletClient.createWalletWhenRegister(users.getUserId());
        }catch (Exception e){
            throw new RuntimeException("用户创建钱包失败，请重试");
        }

        return R.ok("用户注册成功");
    }

    //用户登陆
    @Override
    public R<UserLoginVO> login(LoginFormDTO loginFormDTO) {
        log.info("login method is called : LoginInfo{}",loginFormDTO);

        String username = loginFormDTO.getUsername();
        String password = loginFormDTO.getPassword();

        if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)){
            log.error("用户名或密码为空{}",loginFormDTO);
            return R.error("用户名或密码为空,请重试");
        }

        //查询用户是否存在
        Users users = lambdaQuery().eq(Users::getUsername, username).one();
        if (users == null){
            log.error("用户不存在{}",loginFormDTO);
            return R.error("用户不存在,请重试");
        }

        //检查用户是否被锁定
        if (users.getStatus() == UserState.LOCKED){
            log.error("用户已锁定{}",loginFormDTO);
            return R.error("当前用户已锁定,请联系管理员");
        }

        //检查用户状态
        if (users.getStatus() == UserState.CLOSED){
            log.error("用户已关闭{}",loginFormDTO);
            return R.error("当前用户已注销,请联系管理员");
        }


        //验证密码是否正确
        if (!PasswordUtil.matches(password, users.getPassword())){
            log.error("用户名或密码错误{}",loginFormDTO);
            return R.error("用户名或密码错误，请重试");
        }
        //生成token
        String token = jwtUtils.createToken(users.getUserId(),users.getRole().name());

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);
        userLoginVO.setUserId(users.getUserId());
        userLoginVO.setUsername(users.getUsername());
        userLoginVO.setUserRole(users.getRole());

        log.info("userLoginVo:{}",userLoginVO);

        return R.ok(userLoginVO);
    }

    // 关闭账户
    //用户只能注销账户，不能直接删除账户
    //用户注销账户时，需要检查用户钱包是否清零，如果未清零，则不允许注销账户
    @Override
    public R<String> closeAccount(String password) {
        if (StrUtil.isEmpty(password)){
            log.error("密码为空{}",password);
            return R.error("密码为空,请重试");
        }
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

        //检查用户钱包是否清零
        R<WalletVO> walletVoR = walletClient.userGetSelfWalletInfo();
        WalletVO walletVO = walletVoR.getData();
        if (walletVO.getTotalBalance().compareTo(BigDecimal.ZERO)!=0){
            log.error("用户钱包未清零: userId={}", userId);
            return R.error("用户钱包未清零，请先清零钱包");
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
    @Transactional
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

        //管理员删除用户的钱包
        try{
            walletClient.adminDeleteWallet(userIds);
        }catch (Exception e){
            throw new RuntimeException("删除钱包失败，请重试");
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


    //由管理员定期删除注销的用户
    @Override
    @Transactional
    public R<String> deleteUserWithClosedState() {
        String userRole = UserContext.getUserRole();
        Long adminUserId = UserContext.getUserId();

        log.info("userRole:{},userId:{}",userRole,adminUserId);

        // 1. 权限检查
        if (!UserRole.ADMIN.name().equals(userRole)) {
            log.error("当前用户没有管理员权限: userId={}, userRole={}", adminUserId, userRole);
            return R.error("当前用户没有管理员权限");
        }
        log.info("权限正常");

        // 2. 删除所有状态为 CLOSED 的用户
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getStatus, UserState.CLOSED.name());
        List<Long> userIds = Optional.ofNullable(this.list(queryWrapper))
                .orElse(Collections.emptyList())
                .stream()
                .map(Users::getUserId)
                .collect(Collectors.toList());
        log.info("userIds:{}",userIds);
        // 3. 判断是否有需要删除的用户
        if (CollectionUtil.isEmpty(userIds)) {
            log.warn("没有需要删除的用户: adminUserId={}", adminUserId);
            return R.ok("没有需要删除的用户");
        }
        log.info("有需要删除的用户");
        //管理员删除用户的钱包
        try{
            walletClient.adminDeleteWallet(userIds);
        }catch (Exception e){
            throw new RuntimeException("删除钱包失败，请重试");
        }

        // 4. 执行删除
        try{
            this.removeByIds(userIds);
        }catch (Exception e){
            throw new RuntimeException("删除用户失败，请重试");
        }

        log.info("管理员删除了 {} 个已注销的用户: adminUserId={}", userIds.size(), adminUserId);
        return R.ok("成功删除 " + userIds.size() + " 个用户");
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
        return updateUserField(oldPassword, newPassword, "密码",
                user -> user.getPassword(),
                (user, value) -> user.setPassword(PasswordUtil.encryptPassword(value)),
                PasswordUtil::matches);
    }

    @Override
    public R<String> updatePhoneNumber(String oldNumber, String newNumber) {
        if (isPhoneNumberExists(newNumber)){
            return R.error("该手机号已存在");
        }

        return updateUserField(oldNumber, newNumber, "手机号",
                Users::getPhoneNumber,
                Users::setPhoneNumber,
                String::equals);
    }

    @Override
    public R<String> updateEmail(String oldEmail, String newEmail) {
        if (isEmailExists(newEmail)){
            return R.error("该邮箱已存在");
        }
        return updateUserField(oldEmail, newEmail, "邮箱",
                Users::getEmail,
                Users::setEmail,
                String::equals);
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

    @Override
    public R<Page<Users>> getUsersWithStatus(int page, int pageSize, UserState status) {
        log.info("getUsersWithStatus: page={}, pageSize={}, status={}", page, pageSize, status);

        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();

        if (userId == null || userRole == null) {
            log.error("用户未登录");

            return R.error("用户未登录");
        }

        log.info("getUsersWithStatus: userId={}, userRole={}", userId, userRole);

        if (!UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户无权限: userRole={}", userRole);
            return R.error("用户无权限");
        }

        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getStatus, status)
                .orderByDesc(Users::getLastLogin);

        Page<Users> usersPage = new Page<>(page, pageSize);
        this.page(usersPage, usersLambdaQueryWrapper);

        return R.ok(usersPage);
    }

    /**
     * 通用更新用户字段方法
     * @param oldValue 旧值
     * @param newValue 新值
     * @param fieldName 字段名称
     * @param getter 获取当前用户字段的方法
     * @param setter 更新用户字段的方法
     * @param comparator 旧值和新值的比较方法
     * @return 统一的 R<String> 响应
     */
    private R<String> updateUserField(String oldValue, String newValue, String fieldName,
                                      Function<Users, String> getter, BiConsumer<Users, String> setter,
                                      BiPredicate<String, String> comparator) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        if (StrUtil.isEmpty(newValue)) {
            log.error("新{}不能为空", fieldName);
            return R.error(fieldName + "不能为空");
        }

        Users user = this.getById(userId);
        if (user == null) {
            log.error("用户信息不存在: userId={}", userId);
            return R.error("用户信息异常，请重新登录");
        }

        String currentValue = getter.apply(user);
        boolean isFirstTimeSet = StrUtil.isEmpty(currentValue);

        // 如果是第一次设置（当前值为空），则不需要校验旧值
        if (!isFirstTimeSet) {
            // 只有当不是第一次设置时，才检查旧值
            if (StrUtil.isEmpty(oldValue)) {
                log.error("用户未提供旧{}", fieldName);
                return R.error("请输入旧" + fieldName);
            }

            // 校验旧值是否正确
            if (!comparator.test(oldValue, currentValue)) {
                log.error("{}验证失败: userId={}", fieldName, userId);
                return R.error("旧" + fieldName + "错误");
            }

            // 新值不能与旧值相同
            if (comparator.test(newValue, currentValue)) {
                log.error("新{}不能与旧{}相同: userId={}", fieldName, fieldName, userId);
                return R.error("新" + fieldName + "不能与旧" + fieldName + "相同");
            }
        }

        // 更新字段
        Users updateUser = new Users();
        updateUser.setUserId(userId);
        setter.accept(updateUser, newValue);

        boolean res = this.updateById(updateUser);
        if (!res) {
            log.error("更新{}失败: userId={}", fieldName, userId);
            return R.error("更新" + fieldName + "失败，请稍后重试");
        }

        String logMessage = isFirstTimeSet ? "用户首次设置{}: userId={}" : "用户{}更新成功: userId={}";
        log.info(logMessage, fieldName, userId);

        String successMessage = isFirstTimeSet ? "设置" + fieldName + "成功" : "更新" + fieldName + "成功";
        return R.ok(successMessage);
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