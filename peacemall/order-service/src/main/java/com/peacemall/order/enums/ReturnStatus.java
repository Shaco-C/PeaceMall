package com.peacemall.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 退货状态枚举
 */
@Getter
public enum ReturnStatus {
    NOT_REQUESTED("NOT_REQUESTED"),  // 未请求
    REQUESTED("REQUESTED"),          // 申请退货
    APPROVED("APPROVED"),            // 退货请求通过
    REJECTED("REJECTED"),            // 退货请求被拒绝
    RETURNED("RETURNED");            // 已退货

    @EnumValue
    private final String value;

    ReturnStatus(String value) {
        this.value = value;
    }
}
