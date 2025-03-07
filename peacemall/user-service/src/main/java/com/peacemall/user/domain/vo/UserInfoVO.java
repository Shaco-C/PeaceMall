package com.peacemall.user.domain.vo;


import com.peacemall.common.enums.UserRole;
import com.peacemall.user.enums.UserState;
import lombok.Data;

@Data
public class UserInfoVO {
    private String username;

    private String nickname;

    private String email;

    private String phoneNumber;

    private UserRole role;

    private UserState status;
    private String avatarUrl;
    private String signature;

}
