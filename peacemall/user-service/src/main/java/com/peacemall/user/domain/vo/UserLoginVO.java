package com.peacemall.user.domain.vo;

import com.peacemall.common.enums.UserRole;
import lombok.Data;


@Data
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
    private UserRole userRole;
}