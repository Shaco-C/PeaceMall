package com.peacemall.logs.listener;


import com.peacemall.common.constant.FlowLogsMQConstants;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.logs.service.DeadLetterLogService;
import com.peacemall.logs.service.FlowLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author watergun
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FlowLogDeadLetterListener {

    private final FlowLogService flowLogService;

    private final DeadLetterLogService deadLetterLogService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = FlowLogsMQConstants.DLX_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = FlowLogsMQConstants.DLX_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = FlowLogsMQConstants.DLX_ROUTING_KEY
    ))
    public void processDeadLetter(FlowLogsDTO flowLogsDTO) {
        log.warn("接收到死信队列消息: {}", flowLogsDTO);

        try {
            // 直接保存到死信日志表，不再尝试重新处理
            deadLetterLogService.saveToDeadLetterDatabase(flowLogsDTO,"添加流水日志失败，进入死信队列，存储到日志中");
        } catch (Exception e) {
            // 捕获所有异常，确保不会影响消息确认
            log.error("记录死信消息时发生异常: {}", flowLogsDTO, e);
            throw new RuntimeException("记录死信消息时发生异常");
        }
    }
}