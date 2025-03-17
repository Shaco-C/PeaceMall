package com.peacemall.logs.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum DeadLetterLogStatus {
    PENDING("PENDING"),
    RESOLVED("RESOLVED"),
    FAILED("FAILED");

    @EnumValue
    private final String value;

    DeadLetterLogStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
