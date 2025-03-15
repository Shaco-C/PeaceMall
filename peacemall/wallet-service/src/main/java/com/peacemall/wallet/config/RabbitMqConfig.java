package com.peacemall.wallet.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@Configuration
//@RequiredArgsConstructor
public class RabbitMqConfig {

//    private final RabbitTemplate rabbitTemplate;
//
//    @PostConstruct
//    public void initRabbitTemplate() {
//        // 设置 JSON 消息转换器，避免 messageConverter 不生效
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//
//        // 配置 Publisher Confirm 回调
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                log.info("✅ 消息成功投递到交换机，ID: {}", correlationData != null ? correlationData.getId() : "null");
//            } else {
//                log.error("❌ 消息未成功投递到交换机，原因: {}", cause);
//            }
//        });
//
//        // 配置 Publisher Returns 回调
//        rabbitTemplate.setReturnsCallback(returnedMessage -> {
//            log.error("⚠️ 消息无法投递到队列！Exchange: {}, RoutingKey: {}, Message: {}",
//                    returnedMessage.getExchange(), returnedMessage.getRoutingKey(), returnedMessage.getMessage());
//        });
//    }
}
