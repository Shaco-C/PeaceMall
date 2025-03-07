package com.peacemall.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserRole {
    ADMIN("ADMIN"),
    MERCHANT("MERCHANT"),
    USER("USER");

    @EnumValue
    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
