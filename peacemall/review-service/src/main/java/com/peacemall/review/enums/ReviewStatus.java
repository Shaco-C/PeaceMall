package com.peacemall.review.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ReviewStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    @EnumValue
    private final String value;

    ReviewStatus(String value) {
        this.value = value;
    }

}
