package com.peachmall.search.listener;


//监听商店服务中的商店数据变化,同步到es中

import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.domain.dto.ShopDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.service.EsShopOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EsShopOperationListener {
    private final EsShopOperationService esShopOperationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_SHOP_ADD_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_SHOP_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_ADD_SHOP_ROUTING_KEY
    ))
    public void addShopDoc(@Payload ShopDTO shopDTO){

        if(shopDTO == null){
            log.error("商家信息为空，无法添加到es中");
            return;
        }
        try{
            esShopOperationService.addShopDoc(shopDTO);
        }catch (Exception e){
            log.error("添加商家到es中失败，失败的商店日志信息为:{}",shopDTO);
            throw new RuntimeException("添加商家到es中失败");
        }


    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_SHOP_UPDATE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_SHOP_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_UPDATE_SHOP_ROUTING_KEY
    ))
    public void updateShopDoc(@Payload ShopDTO shopDTO){
        if(shopDTO == null){
            log.error("商店信息为空，无法更新到es中");
            return;
        }
        try{
            esShopOperationService.updateShopDoc(shopDTO);
        }catch (DTONotFoundException e){
            //手动捕获异常，避免不必要的重试
            log.error("更新商店到es中失败，失败的商店日志信息为:{}",shopDTO);
        }catch (Exception e){
            log.error("更新商店到es中失败，失败的商店日志信息为:{}",shopDTO);
            throw new RuntimeException("更新商店到es中失败");
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_SHOP_DELETE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_SHOP_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_DELETE_SHOP_ROUTING_KEY
    ))
    public void deleteShopDocs(@Payload List<Long> shopIds){
        if(shopIds.isEmpty()){
            log.error("商店id为空，无法删除");
            return;
        }
        try{
            esShopOperationService.deleteShopDoc(shopIds);
        }catch (Exception e){
            log.error("删除商店失败,{}",shopIds);
            throw new RuntimeException("删除商店失败");
        }
    }



}
