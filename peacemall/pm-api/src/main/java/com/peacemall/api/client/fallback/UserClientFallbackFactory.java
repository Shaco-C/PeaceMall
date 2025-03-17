package com.peacemall.api.client.fallback;

import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.UserDTO;
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
                log.error("adminChangeUserRole error cause", cause);
                log.error("userId:{},userRole:{},用户角色修改失败，请重试",userId, userRole);
                throw new RuntimeException("userId:{},userRole:{},用户角色修改失败，请重试"+cause);
            }

            @Override
            public PageDTO<UserDTO> findAllUsersWithPage(int page, int pageSize) {
                log.error("findAllUsersWithPage error cause", cause);
                log.error("page:{},size:{},分页查询用户信息失败，请重试",page,pageSize);
                throw new RuntimeException("page:{},size:{},分页查询用户信息失败，请重试"+cause);
            }
        };
    }
}
