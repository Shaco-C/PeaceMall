package com.peacemall.common.constant;

public interface FlowLogsMQConstants {
    String FLOW_LOGS_EXCHANGE_NAME ="log.direct";
    String FLOW_LOGS_QUEUE_NAME = "log.flow.direct.queue";
    String FLOW_LOGS_ROUTING_KEY = "log.flow";

    String DLX_EXCHANGE_NAME = "log.dlx.direct";
    String DLX_QUEUE_NAME = "log.flow.dlq";
    String DLX_ROUTING_KEY = "log.flow.dead";

}
