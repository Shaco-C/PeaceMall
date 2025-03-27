package com.peacemall.common.constant;

public interface ProductConfigurationMQConstant {
    String PRODUCT_CONFIGURATION_EXCHANGE = "product.configuration.direct"; // 交换机名称
    String PRODUCT_CONFIGURATION_QUEUE = "product.configuration.queue"; // 变更日志队列
    String PRODUCT_CONFIGURATION_UPDATE_ROUTING_KEY = "product.configuration.update"; // 路由键
}
