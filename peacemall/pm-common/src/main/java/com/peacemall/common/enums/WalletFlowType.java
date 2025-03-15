package com.peacemall.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author watergun
 */

@Getter
public enum WalletFlowType {
    RECHARGE("充值"),
    WITHDRAWAL("提现"),
    EXPENSE("消费"),
    PENDING_CONFIRM("待确认金额确认"),
    REFUND("退款");

    @EnumValue
    private final String description;

    WalletFlowType(String description) {
        this.description = description;
    }
}
