package com.peacemall.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author watergun
 */

public enum ProductStockMode {
    NORMAL("NORMAL"),
    PRE_SALE("PRE_SALE");

    @EnumValue
    private final String value;

    ProductStockMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
