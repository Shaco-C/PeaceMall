package com.peacemall.common.constant;

public interface StockChangeLogMQConstant {

    String STOCK_LOG_EXCHANGE = "stock.log.direct"; // 交换机名称
    String STOCK_LOG_QUEUE = "stock.log.queue"; // 变更日志队列
    String STOCK_LOG_ADD_ROUTING_KEY = "stock.log.add"; // 路由键
}
