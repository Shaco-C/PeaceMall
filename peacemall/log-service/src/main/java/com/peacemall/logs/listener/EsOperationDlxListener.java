package com.peacemall.logs.listener;

import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.logs.service.DeadLetterLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EsOperationDlxListener {

    private final DeadLetterLogService deadLetterLogService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_OPERATION_DLX_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY
    ))
    public void processDeadLetter(String message) {
        log.warn("接收到死信队列消息: {}", message);

        try{
            deadLetterLogService.saveToDeadLetterDatabase(message,"信息存储到es中失败");
        }catch (Exception e){
            log.error("信息存储到es中失败");
            throw new RuntimeException("信息存储到es中失败");
        }
    }
}
