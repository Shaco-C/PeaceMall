package com.peacemall.product.listener;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.constant.ProductConfigurationMQConstant;
import com.peacemall.product.service.ProductConfigurationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductConfigurationListener {

    private final ProductConfigurationsService productConfigurationsService;

    /**
     * 更新库存,取消订单之类的
     * @param message configId以及库存变化 组成的Map
     * @return
     * @author watergun
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = ProductConfigurationMQConstant.PRODUCT_CONFIGURATION_QUEUE, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = ProductConfigurationMQConstant.PRODUCT_CONFIGURATION_EXCHANGE),
            key = ProductConfigurationMQConstant.PRODUCT_CONFIGURATION_UPDATE_ROUTING_KEY
    ))
    public void updateProductConfigurationStocks(@Payload String message){
        log.info("接收到库存变更消息: {}", message);
        if (StrUtil.isEmpty(message)){
            log.error("库存变更消息为空");
            return;
        }
        // 处理库存变更消息
        // 先转变为Map类型
        Map<Long, Integer> configIdAndQuantityMap = JSONUtil.toBean(
                message,
                new TypeReference<Map<Long, Integer>>() {},
                false
        );
        if (configIdAndQuantityMap == null){
            log.error("库存变更消息转换失败");
            throw new RuntimeException("库存变更消息转换失败,重试");
        }
        // 更新库存
        log.info("更新库存,信息为:{}", configIdAndQuantityMap);
        try{
            productConfigurationsService.updateProductConfigurationsQuantity(configIdAndQuantityMap);
        }catch (Exception e){
            log.error("更新库存失败,信息为:{}", configIdAndQuantityMap);
            throw new RuntimeException("更新库存失败,重试");
        }

        log.info("更新库存成功");

    }
}
