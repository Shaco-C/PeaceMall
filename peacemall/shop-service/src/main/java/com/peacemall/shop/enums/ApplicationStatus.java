package com.peacemall.shop.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("PENDING"),

    APPROVED("APPROVED"),

    REJECTED("REJECTED");

    @EnumValue
    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }
}
