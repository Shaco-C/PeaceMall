package com.peacemall.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author watergun
 */

public enum ProductStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    @EnumValue
    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
