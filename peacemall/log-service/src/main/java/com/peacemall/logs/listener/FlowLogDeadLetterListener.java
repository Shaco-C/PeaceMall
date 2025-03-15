package com.peacemall.logs.listener;


import com.peacemall.common.constant.FlowLogsMQConstants;
import com.peacemall.common.domain.dto.FlowLogsDTO;
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
public class FlowLogDeadLetterListener {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = FlowLogsMQConstants.DLX_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = FlowLogsMQConstants.DLX_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = FlowLogsMQConstants.DLX_ROUTING_KEY
    ))
    public void processDeadLetter(FlowLogsDTO flowLogsDTO) {
        log.warn("处理死信队列消息: {}", flowLogsDTO);

//        try {
//            // 可以尝试重新处理
//            // 或者实现更复杂的补偿逻辑
//            flowLogService.addFlowLogWithRetry(flowLogsDTO);
//            log.info("死信队列消息处理成功: {}", flowLogsDTO);
//        } catch (Exception e) {
//            log.error("死信队列消息处理最终失败，需要人工干预: {}", flowLogsDTO, e);
//            // 可以将消息保存到数据库中的特殊表中，供后续人工处理
//            // 或发送告警通知
//        }
    }
}