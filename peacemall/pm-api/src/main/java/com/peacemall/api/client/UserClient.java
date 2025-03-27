package com.peacemall.api.client;

import com.peacemall.api.client.fallback.UserClientFallbackFactory;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.UserAddressDTO;
import com.peacemall.common.domain.dto.UserDTO;
import com.peacemall.common.enums.UserRole;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service" , fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @PutMapping("/user/admin/changeUserRole")
    void adminChangeUserRole(@RequestParam(value = "userId") Long userId,
                                    @RequestParam(value = "userRole") UserRole userRole);

    //分页查询所有的用户信息
    //用于es数据的批量插入

    @GetMapping("/user/admin/findAllUsersWithPage")
    PageDTO<UserDTO> findAllUsersWithPage(@RequestParam(value = "page",defaultValue = "1")int page,
                                                 @RequestParam(value = "pageSize",defaultValue = "1000")int pageSize);


    @GetMapping("/addresses/getAddressById")
    UserAddressDTO getUserAddressById(@RequestParam Long addressId);
}
