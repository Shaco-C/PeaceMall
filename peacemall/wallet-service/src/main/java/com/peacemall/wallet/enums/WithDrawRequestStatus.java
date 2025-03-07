package com.peacemall.wallet.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author watergun
 */

public enum WithDrawRequestStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED"),
    COMPLETED("COMPLETED");

    @EnumValue
    private final String value;

    WithDrawRequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
