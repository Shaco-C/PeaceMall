package com.peacemall.wallet.listener;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.constant.WallerMQConstant;
import com.peacemall.common.domain.dto.WalletAmountChangeDTO;
import com.peacemall.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletListener {

    private final WalletService walletService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = WallerMQConstant.WALLET_QUEUE, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = WallerMQConstant.WALLET_EXCHANGE),
            key = WallerMQConstant.WALLET_UPDATE_PENDING_BALANCE_BY_USERID_ROUTING_KEY
    ))
    public void updateUserPendingBalance(@Payload String message) {
        log.info("updateUserPendingBalance: received message: {}", message);
        if (StrUtil.isEmpty(message)) {
            log.error("updateUserPendingBalance: message is empty");
            throw new RuntimeException("消息为空");
        }
        try {
            WalletAmountChangeDTO walletAmountChangeDTO = JSONUtil.toBean(message, WalletAmountChangeDTO.class);
            walletService.userWalletPendingAmountChange(walletAmountChangeDTO);
        }catch (Exception e){
            log.error("updateUserPendingBalance: message parse failed, message: {}", message, e);
            throw new RuntimeException("用户待确认金额变动失败");
        }
    }
}
