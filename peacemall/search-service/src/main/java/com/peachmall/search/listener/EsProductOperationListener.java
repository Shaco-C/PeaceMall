package com.peachmall.search.listener;

//监听商品服务中的商品数据变化,同步到es中

import com.peacemall.common.constant.EsOperataionMQConstant;

import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.service.EsProductOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EsProductOperationListener {

    private final EsProductOperationService esProductOperationService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_PRODUCT_ADD_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_ADD_PRODUCT_ROUTING_KEY
    ))
    public void addProductDoc(@Payload ProductDTO productDTO){

        if(productDTO == null){
            log.error("商品信息为空，无法添加到es中");
            return;
        }
        try{
            esProductOperationService.addProductDocs(productDTO);
        }catch (Exception e){
            log.error("添加商品到es中失败，失败的商品日志信息为:{}",productDTO);
            throw new RuntimeException("添加商品到es中失败");
        }


    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_PRODUCT_UPDATE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_UPDATE_PRODUCT_ROUTING_KEY
    ))
    public void updateProductDoc(@Payload ProductDTO productDTO){
        if(productDTO == null){
            log.error("商品信息为空，无法更新到es中");
            return;
        }
        try{
            esProductOperationService.updateProductDocs(productDTO);
        }catch (DTONotFoundException e){
            //手动捕获异常，避免不必要的重试
            log.error("更新商品到es中失败，失败的商品日志信息为:{}",productDTO);
        }catch (Exception e){
            log.error("更新商品到es中失败，失败的商品日志信息为:{}",productDTO);
            throw new RuntimeException("更新商品到es中失败");
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EsOperataionMQConstant.ES_PRODUCT_DELETE_QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME, durable = "true", type = ExchangeTypes.DIRECT),
            key = EsOperataionMQConstant.ES_DELETE_PRODUCT_ROUTING_KEY
    ))
    public void deleteProductDocs(@Payload List<Long> productIds){
        if(productIds.isEmpty()){
            log.error("商品id为空，无法删除");
            return;
        }
        try{
            esProductOperationService.deleteProductDocs(productIds);
        }catch (Exception e){
            log.error("删除商品失败,{}",productIds);
            throw new RuntimeException("删除商品失败");
        }
    }

}
