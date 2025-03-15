package com.peacemall.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author watergun
 */

public enum StockSourceType {
    SALED("SALED"),
    MUNAL_ADD("MUNAL_ADD"),
    MUNAL_DECREASE("MUNAL_DECREASE");
    @EnumValue
    private final String value;

    StockSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
