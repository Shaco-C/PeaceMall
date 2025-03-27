package com.peacemall.common.constant;

public interface ShopMQConstant {
    String SHOP_EXCHANGE = "shop.direct"; // 交换机名称
    String SHOP_QUEUE = "shop.queue"; // 变更日志队列
    String SHOP_UPDATE_PENDING_BALANCE_BY_SHOPID_ROUTING_KEY = "shop.pending.balance.shopId.update"; // 路由键

}
