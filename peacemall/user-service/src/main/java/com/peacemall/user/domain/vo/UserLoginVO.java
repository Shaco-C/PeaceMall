package com.peacemall.user.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
}