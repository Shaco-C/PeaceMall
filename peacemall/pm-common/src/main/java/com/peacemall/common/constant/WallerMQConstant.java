package com.peacemall.common.constant;

public interface WallerMQConstant {

    String WALLET_EXCHANGE = "wallet.direct"; // 交换机名称
    String WALLET_QUEUE = "wallet.queue"; // 变更日志队列
    String WALLET_UPDATE_PENDING_BALANCE_BY_USERID_ROUTING_KEY = "wallet.pending.balance.userId.update"; // 路由键

}
