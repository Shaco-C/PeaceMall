package com.peacemall.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.user.domain.po.UserAddress;
import com.peacemall.user.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "用户地址服务接口")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    //用户获取地址信息
    //一个用户最多有8条地址信息，不需要Page返回
    @ApiOperation(value = "获取用户地址信息")
    @GetMapping("/list")
    public R<List<UserAddress>> queryUserAddressByUserId() {
        return userAddressService.queryUserAddressByUserId();
    }

    //用户增加地址信息
    //一个用户最多有8条地址信息
    @ApiOperation(value = "增加用户地址信息")
    @PostMapping("/add")
    public R<String> addUserAddress(@RequestBody UserAddress userAddress) {
        return userAddressService.addUserAddress(userAddress);
    }

    //用户将地址信息设置为删除状态
    // 批量删除地址（用户侧软删除）
    @ApiOperation(value = "批量删除用户地址信息")
    @PutMapping("/delete")
    public R<String> batchDeleteAddress(@RequestBody IdsDTO idsDTO){
        return userAddressService.batchDeleteAddress(idsDTO.getIdsList());
    }

    //用户修改地址信息
    @ApiOperation(value = "修改用户地址信息")
    @PutMapping("/update")
    public R<String> updateUserAddress(@RequestBody UserAddress userAddress){
        return userAddressService.updateUserAddress(userAddress);
    }

    //用户设置默认地址
    @ApiOperation(value = "设置用户默认地址")
    @PutMapping("/setDefaultAddress/{userAddressId}")
    public R<String> setDefaultAddress(@PathVariable Long userAddressId){
        return userAddressService.setDefaultAddress(userAddressId);
    }

    // 根据用户ID查询默认地址（用于下单时快速获取）
    @ApiOperation(value = "根据用户ID查询默认地址")
    @GetMapping("/getDefaultAddressByUserId")
    public R<UserAddress> getDefaultAddressByUserId(){
        return userAddressService.getDefaultAddressByUserId();
    }

    // 管理员查询所有已删除的地址（status=0）
    @ApiOperation(value = "管理员查询所有已删除的地址")
    @GetMapping("/admin/listDeletedAddresses")
    public R<Page<UserAddress>> listDeletedAddresses(@RequestParam(value = "page",defaultValue = "1")int page,
                                                     @RequestParam(value = "pageSize",defaultValue = "1")int pageSize){
        return userAddressService.listDeletedAddresses(page, pageSize);
    }


    //管理员删除用户删除的地址信息
    @ApiOperation(value = "管理员删除用户删除的地址信息")
    @DeleteMapping("/admin/deleteDeletedAddress")
    public R<String> adminPhysicallyDeleteAddressWith0Status(){
        return userAddressService.adminPhysicallyDeleteAddressWith0Status();
    }
}
