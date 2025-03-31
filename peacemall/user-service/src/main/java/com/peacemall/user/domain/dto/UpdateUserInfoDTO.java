package com.peacemall.user.domain.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDTO {

    private String nickname;
    private String avatarUrl;
    private String signature;
}
