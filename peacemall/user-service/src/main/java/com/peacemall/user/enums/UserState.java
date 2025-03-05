package com.peacemall.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserState {
    // 正常状态
    ACTIVE("ACTIVE"),
    // 锁定状态-冻结
    LOCKED("LOCKED"),
    // 待审核：用户被冻结之后申请解禁或用户被举报后的审核
    PENDING("PENDING"),
    // 用户注销
    CLOSED("CLOSED");

    @EnumValue
    private final String value;

    UserState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
