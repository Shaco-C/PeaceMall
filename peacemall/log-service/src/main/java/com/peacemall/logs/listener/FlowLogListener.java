package com.peacemall.logs.listener;


import com.peacemall.common.constant.FlowLogsMQConstants;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.logs.service.FlowLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlowLogListener {
    private final FlowLogService flowLogService ;

    //监听创建日志的信息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = FlowLogsMQConstants.FLOW_LOGS_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = FlowLogsMQConstants.DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = FlowLogsMQConstants.DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = FlowLogsMQConstants.FLOW_LOGS_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = FlowLogsMQConstants.FLOW_LOGS_ROUTING_KEY
    ))
    public void addFlowLog(@Payload FlowLogsDTO flowLogsDTO) {

        if (flowLogsDTO == null) {
            log.warn("接收到空的日志消息，忽略处理");
            return;
        }
        try {
            flowLogService.addFlowLog(flowLogsDTO);
        } catch (Exception e) {
            log.error("日志存储失败，消息内容：{}", flowLogsDTO, e);
            throw new RuntimeException("日志存储失败,重试,然后发送到死信队列");
        }
    }


}
