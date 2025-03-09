package com.peacemall.api.client;

import com.peacemall.api.client.fallback.UserClientFallbackFactory;
import com.peacemall.common.enums.UserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service" , fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @PutMapping("/admin/changeUserRole")
    void adminChangeUserRole(@RequestParam(value = "userId") Long userId,
                                    @RequestParam(value = "userRole") UserRole userRole);

}
