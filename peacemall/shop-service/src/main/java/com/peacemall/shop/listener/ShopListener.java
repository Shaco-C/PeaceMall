package com.peacemall.shop.listener;


import cn.hutool.json.JSONUtil;
import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.constant.ShopMQConstant;
import com.peacemall.common.domain.dto.WalletAmountChangeDTO;
import com.peacemall.shop.service.ShopsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopListener {

//    private final ShopsService shopsService;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = ShopMQConstant.SHOP_QUEUE, durable = "true",
//                    arguments = {
//                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
//                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
//                    }),
//            exchange = @Exchange(name = ShopMQConstant.SHOP_EXCHANGE),
//            key = ShopMQConstant.SHOP_UPDATE_PENDING_BALANCE_BY_SHOPID_ROUTING_KEY
//    ))
//    public void merchantPendingBalanceChanged(@Payload String message) {
//        log.info("merchantPendingBalanceChanged: received message: {}", message);
//
//        try{
//            WalletAmountChangeDTO walletAmountChangeDTO = JSONUtil.toBean(message, WalletAmountChangeDTO.class);
//            shopsService.merchantPendingBalanceChange(walletAmountChangeDTO);
//        }catch (Exception e){
//            log.error("merchantPendingBalanceChanged: message parse failed, message: {}", message, e);
//            throw new RuntimeException("商家待确认金额变动失败");
//        }
//    }
}
