package com.peacemall.common.constant;

public interface CartItemMQConstant {
    String CART_ITEM_EXCHANGE = "cartItem.direct"; // 交换机名称
    String CART_ITEM_QUEUE = "cartItem.queue"; // 变更日志队列
    String CART_ITEM_DELETE_ROUTING_KEY = "cartItem.delete"; // 路由键
}
