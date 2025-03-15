package com.peacemall.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.peacemall.common.utils.RabbitMqHelper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")  // 只有配置了RabbitMQ才启用
@AutoConfigureAfter(RabbitAutoConfiguration.class)  // 在RabbitMQ自动配置后执行
public class RabbitMqHelperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageConverter messageConverter(ObjectMapper mapper){
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        converter.setCreateMessageIds(true);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitMqHelper rabbitMqHelper(RabbitTemplate rabbitTemplate){
        return new RabbitMqHelper(rabbitTemplate);
    }
}