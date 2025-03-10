package com.peacemall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.utils.UserContext;
import com.peacemall.user.domain.po.UserAddress;
import com.peacemall.common.enums.UserRole;
import com.peacemall.user.mapper.UserAddressMapper;
import com.peacemall.user.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public R<List<UserAddress>> queryUserAddressByUserId() {
        Long userId = UserContext.getUserId();

        if (userId == null) {
            log.info("queryUserAddressByUserId failed: 用户未登陆,或系统异常");
            return R.error("用户未登陆,或系统异常");
        }

        log.info("queryUserAddressByUserId is called, userId: {}", userId);

        // 构造查询条件
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getStatus, 1)
                .orderByDesc(UserAddress::getUpdatedAt);

        List<UserAddress> userAddresses = this.list(queryWrapper);

        // 直接返回查询结果
        return R.ok(userAddresses);
    }


    //一个用户最多有8条地址信息
    @Override
    @Transactional
    public R<String> addUserAddress(UserAddress userAddress) {
        log.info("addUserAddress is called, userAddress: {}", userAddress);
        if (userAddress == null) {
            log.info("addUserAddress failed: 请求参数为空");
            return R.error("请求参数为空");
        }
        Long userId = UserContext.getUserId();

        if (userId == null) {
            log.info("addUserAddress failed: 用户未登陆,或系统异常");
            return R.error("用户未登陆,或系统异常");
        }

        log.info("addUserAddress is called, userId: {}", userId);

        // 检查用户是否已经达到地址数量上限
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getStatus, 1);
        long count = this.count(queryWrapper);

        if (count >= 8) {
            log.info("addUserAddress failed: 用户地址数量已达到上限, userId: {}", userId);
            return R.error("用户地址数量已达到上限");
        }

        userAddress.setUserId(userId);
        userAddress.setStatus(1);

        // 如果当前地址是默认地址，则取消用户的其他默认地址
        if (Boolean.TRUE.equals(userAddress.getIsDefault())) {
            setAllAddressesToNotDefault(userId);
        }

        boolean save = this.save(userAddress);
        return save ? R.ok("添加地址成功") : R.error("添加地址失败");
    }

    @Override
    @Transactional
    public R<String> batchDeleteAddress(List<Long> addressIds) {
        log.info("batchDeleteAddress is called, addressIds: {}", addressIds);
        if (addressIds.isEmpty()){
            log.info("要删除的地址为空");
            return R.error("要删除的地址为空");
        }
        Long userId = UserContext.getUserId();

        if (userId == null) {
            log.info("batchDeleteAddress failed: 用户未登陆,或系统异常");
            return R.error("用户未登陆,或系统异常");
        }

        log.info("batchDeleteAddress is called, userId: {}, addressIds: {}", userId, addressIds);



        // 获取数据库中存在的地址，并以 id 作为 key 存入 Map
        List<UserAddress> addressList = this.listByIds(addressIds).stream()
                .filter(address -> userId.equals(address.getUserId())) // 只保留该用户的地址
                .collect(Collectors.toList());

        // 如果 `addressList` 的 key 数量小于 `addressIds`，说明用户试图删除不属于自己的地址
        if (addressList.size() < addressIds.size()) {
            log.info("batchDeleteAddress failed: 用户没有权限删除不属于他的地址, userId: {}", userId);
            return R.error("用户没有权限删除不属于他的地址");
        }
        if (addressList.isEmpty()) {
            log.info("batchDeleteAddress failed: 用户没有要删除的地址, userId: {}", userId);
            return R.error("用户没有要删除的地址");
        }

        //将状态设置为0
        addressList.forEach(address -> address.setStatus(0));

        // 批量删除
        boolean res = this.updateBatchById(addressList);
        return res ? R.ok("批量删除地址成功") : R.error("批量删除地址失败");
    }


    @Override
    @Transactional
    public R<String> updateUserAddress(UserAddress userAddress) {
        log.info("updateUserAddress is called, userAddress: {}", userAddress);
        if (userAddress == null) {
            log.info("updateUserAddress failed: 请求参数为空");
            return R.error("请求参数为空");
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.info("updateUserAddress failed: 用户未登陆,或系统异常");
            return R.error("用户未登陆,或系统异常");
        }

        if (userAddress.getAddressId() == null) {
            log.info("updateUserAddress failed: userAddress or addressId is null");
            return R.error("无效的地址信息");
        }

        // 根据地址 ID 查询数据库
        UserAddress address = this.getById(userAddress.getAddressId());

        if (address == null) {
            log.info("updateUserAddress failed: address not found, addressId: {}", userAddress.getAddressId());
            return R.error("不存在该地址,请刷新重试");
        }

        if (!Objects.equals(address.getUserId(), userId)) {
            log.warn("updateUserAddress failed: 用户没有权限修改该地址, userId: {}, addressId: {}", userId, userAddress.getAddressId());
            return R.error("用户没有权限修改不属于他的地址");
        }

        // 如果当前地址是默认地址，则取消用户的其他默认地址
        if (Boolean.TRUE.equals(userAddress.getIsDefault())) {
            setAllAddressesToNotDefault(userId);
        }

        boolean res = this.updateById(userAddress);
        return res ? R.ok("修改地址成功") : R.error("修改地址失败");
    }

    @Override
    @Transactional
    public R<String> setDefaultAddress(Long userAddressId) {
        log.info("setDefaultAddress is called, userAddressId: {}", userAddressId);
        if (userAddressId == null) {
            log.info("setDefaultAddress failed: 请求参数为空");
            return R.error("请求参数为空");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.info("setDefaultAddress failed: 用户未登录或系统异常");
            return R.error("用户未登录或系统异常");
        }
        log.info("setDefaultAddress is called, userId: {}", userId);

        // 先检查地址是否存在，并且属于该用户
        UserAddress address = this.getById(userAddressId);
        if (address == null) {
            log.info("setDefaultAddress failed: 地址不存在, userAddressId: {}", userAddressId);
            return R.error("不存在该地址，请刷新重试");
        }
        if (!Objects.equals(address.getUserId(), userId)) {
            log.warn("setDefaultAddress failed: 用户没有权限修改该地址, userId: {}, userAddressId: {}", userId, userAddressId);
            return R.error("用户没有权限修改不属于他的地址");
        }

        // 取消当前默认地址（如果有的话）
        setAllAddressesToNotDefault(userId);

        // 更新该地址为默认
        boolean res = this.lambdaUpdate()
                .eq(UserAddress::getAddressId, userAddressId)
                .eq(UserAddress::getUserId, userId)  // 确保用户只能操作自己的地址
                .set(UserAddress::getIsDefault, true)
                .update();

        return res ? R.ok("设置默认地址成功") : R.error("设置默认地址失败");
    }


    @Override
    public R<UserAddress> getDefaultAddressByUserId() {
        Long userId = UserContext.getUserId();
        log.info("getDefaultAddressByUserId is called, userId: {}", userId);

        if (userId == null) {
            log.warn("getDefaultAddressByUserId failed: 用户未登录或系统异常");
            return R.error("用户未登录或系统异常");
        }

        // 查询默认地址
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, true);

        UserAddress defaultAddress = this.getOne(queryWrapper, false); // 避免抛异常

        if (defaultAddress == null) {
            log.info("getDefaultAddressByUserId result: 用户没有默认地址, userId: {}", userId);
            return R.ok(new UserAddress());
        }

        return R.ok(defaultAddress);
    }


    @Override
    public R<Page<UserAddress>> listDeletedAddresses(int page, int pageSize) {
        log.info("listDeletedAddresses is called, page: {}, pageSize: {}", page, pageSize);
        if (page < 1 || pageSize < 1) {
            log.info("listDeletedAddresses failed: 请求参数错误, page: {}, pageSize: {}", page, pageSize);
            return R.error("请求参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("listDeletedAddresses is called, page: {}, pageSize: {}, userId: {}, userRole: {}", page, pageSize, userId, userRole);

        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.warn("listDeletedAddresses failed: 用户未登录或权限错误, userId: {}, userRole: {}", userId, userRole);
            return R.error("用户未登录或权限错误");
        }

        // 查询逻辑
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getStatus, 0)
                .orderByDesc(UserAddress::getUpdatedAt);

        Page<UserAddress> addressPage = new Page<>(page, pageSize);
        this.page(addressPage, queryWrapper);

        if (addressPage.getRecords().isEmpty()) {
            log.info("listDeletedAddresses result: 无已删除地址, page: {}, pageSize: {}", page, pageSize);
        }

        return R.ok(addressPage);
    }


    @Override
    @Transactional
    public R<String> adminPhysicallyDeleteAddressWith0Status() {
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("adminPhysicallyDeleteAddressWith0Status is called, userId: {}, userRole: {}", userId, userRole);

        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.warn("adminPhysicallyDeleteAddressWith0Status failed: 用户未登录或权限错误, userId: {}, userRole: {}", userId, userRole);
            return R.error("用户未登录或权限错误");
        }

        // 统计即将删除的数据量
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getStatus, 0);

        long count = this.count(queryWrapper);
        if (count == 0) {
            log.info("adminPhysicallyDeleteAddressWith0Status: 没有找到 status=0 的地址数据，无需删除");
            return R.ok("无可删除的数据");
        }

        // 执行删除
        boolean deleted = this.remove(queryWrapper);
        if (deleted) {
            log.info("adminPhysicallyDeleteAddressWith0Status success: 物理删除 {} 条地址记录", count);
            return R.ok("删除成功，已删除 " + count + " 条记录");
        } else {
            log.error("adminPhysicallyDeleteAddressWith0Status failed: 数据库删除操作未生效");
            return R.error("删除失败");
        }
    }


    // methods

    /**
     * 将用户的所有默认地址取消默认
     */
    /**
     * 取消该用户的其他默认地址
     */
    private void setAllAddressesToNotDefault(Long userId) {
        // 先检查用户是否有默认地址，减少不必要的更新
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, true);

        long count = this.count(queryWrapper);
        if (count == 0) {
            log.info("setAllAddressesToNotDefault skipped, userId: {} has no default address", userId);
            return;
        }

        // 更新数据库，取消所有默认地址
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, true)
                .set(UserAddress::getIsDefault, false);

        boolean updated = this.update(updateWrapper);
        log.info("setAllAddressesToNotDefault executed, userId: {}, updated: {}", userId, updated);
    }
}