package com.peacemall.user.enums;

public enum UserState {
    //正常状态
    ACTIVE,
    //锁定状态-冻结
    LOCKED,
    //待审核
    //用户被冻结之后申请解禁
    //用户被举报之后的审核
    PENDING,
    //用户注销
    CLOSED
}
