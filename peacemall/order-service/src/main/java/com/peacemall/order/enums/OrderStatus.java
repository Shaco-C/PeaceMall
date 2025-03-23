package com.peacemall.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatus {
    PENDING_PAYMENT("PENDING_PAYMENT"),  // 待支付
    PENDING("PENDING"),                  // 已支付待发货
    SHIPPED("SHIPPED"),                  // 已发货
    IN_TRANSIT("IN_TRANSIT"),            // 在途，等待收货
    DELIVERED("DELIVERED"),              // 已送达
    RECEIVED("RECEIVED"),                // 已收货
    CANCELLED("CANCELLED");              // 已取消

    @EnumValue
    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
