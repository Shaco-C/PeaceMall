package com.peacemall.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.user.domain.po.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    //用户获取地址信息
    //一个用户最多有8条地址信息，不需要Page返回
    R<List<UserAddress>> queryUserAddressByUserId();


    //用户增加地址信息
    //一个用户最多有8条地址信息
    R<String> addUserAddress(UserAddress userAddress);

    //用户将地址信息设置为删除状态
    // 批量删除地址（用户侧软删除）
    R<String> batchDeleteAddress(List<Long> addressIds);

    //用户修改地址信息
    R<String> updateUserAddress(UserAddress userAddress);

    //用户设置默认地址
    R<String> setDefaultAddress(Long userAddressId);

    // 根据用户ID查询默认地址（用于下单时快速获取）
    R<UserAddress> getDefaultAddressByUserId();

    // 管理员查询所有已删除的地址（status=0）
    R<PageDTO<UserAddress>> listDeletedAddresses(int page, int pageSize);

    //管理员删除用户删除的地址信息
    R<String> adminPhysicallyDeleteAddressWith0Status();

}
