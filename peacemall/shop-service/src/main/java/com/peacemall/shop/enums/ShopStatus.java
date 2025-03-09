package com.peacemall.shop.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ShopStatus {

    NORMAL("NORMAL"),

    FROZEN("FROZEN"),

    CLOSED("CLOSED");

    @EnumValue
    private final String value;

    ShopStatus(String value) {
        this.value = value;
    }
}
