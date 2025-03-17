package com.peacemall.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;

    private String username;

    private String nickname;

    private String avatarUrl;

    private String signature;

    private String status;

    private Timestamp lastLogin;
}
