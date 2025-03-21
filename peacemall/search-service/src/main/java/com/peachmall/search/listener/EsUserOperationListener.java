package com.peachmall.search.listener;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.peacemall.common.constant.EsOperataionMQConstant;

import com.peacemall.common.domain.dto.UserDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.service.EsUserOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsUserOperationListener {

    private final EsUserOperationService esUserOperationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_USER_ADD_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_USER_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_ADD_USER_ROUTING_KEY
    ))
    public void addUserDoc(@Payload String message){

        if(message == null){
         log.error("用户信息为空，无法添加到es中");
         return;
        }
        try{
            UserDTO userDTO = JSONUtil.toBean(message, UserDTO.class);
            esUserOperationService.addUserDoc(userDTO);
        }catch (Exception e){
            log.error("添加用户到es中失败，失败的用户日志信息为:{}",message);
            throw new RuntimeException("添加用户到es中失败");
        }


    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_USER_UPDATE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_USER_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_UPDATE_USER_ROUTING_KEY
    ))
    public void updateUserDoc(@Payload String message){
        if(message == null){
            log.error("用户信息为空，无法更新到es中");
            return;
        }
        try{
            UserDTO userDTO = JSONUtil.toBean(message, UserDTO.class);
            esUserOperationService.updateUserDoc(userDTO);
        }catch (DTONotFoundException e){
            //手动捕获异常，避免不必要的重试
            log.error("更新用户到es中失败，失败的用户日志信息为:{}",message);
        }catch (Exception e){
            log.error("更新用户到es中失败，失败的用户日志信息为:{}",message);
            throw new RuntimeException("更新用户到es中失败");
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_USER_DELETE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_USER_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_DELETE_USER_ROUTING_KEY
    ))
    public void deleteUserDocs(@Payload String  message){
        if(message == null){
            log.error("用户id为空，无法删除");
            return;
        }
        List<Long> userIds = JSONUtil.toList(message, Long.class);
        if (CollectionUtil.isEmpty(userIds)){
            log.error("用户id为空，无法删除");
            return;
        }
        try{
            esUserOperationService.deleteUserDoc(userIds);
        }catch (Exception e){
            log.error("删除用户失败,{}",userIds);
            throw new RuntimeException("删除用户失败");
        }
    }


}