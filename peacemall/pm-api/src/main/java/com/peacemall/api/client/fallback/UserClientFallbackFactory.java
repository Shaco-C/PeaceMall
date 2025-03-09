package com.peacemall.api.client.fallback;

import com.peacemall.api.client.UserClient;
import com.peacemall.common.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {

            @Override
            public void adminChangeUserRole(Long userId, UserRole userRole) {
                log.info("adminChangeUserRole error cause", cause);
                log.info("userId:{},userRole:{},用户角色修改失败，请重试",userId, userRole);
                throw new RuntimeException("userId:{},userRole:{},用户角色修改失败，请重试"+cause);
            }
        };
    }
}
