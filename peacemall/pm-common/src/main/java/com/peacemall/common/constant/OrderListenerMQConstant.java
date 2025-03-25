package com.peacemall.common.constant;

public interface OrderListenerMQConstant {

    String ORDER_DIRECT_EXCHANGE = "order.direct";
    String ORDER_DELAY_DIRECT_EXCHANGE = "order.delay.direct";
    String ORDER_DELAY_PAYMENT_STATUS_QUEUE = "order.payment.status.queue";
    String ORDER_DELAY_PAYMENT_STATUS_CHECK_ROUTING_KEY = "order.payment.status.check";

}

