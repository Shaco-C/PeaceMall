package com.peacemall.cartItem.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.peacemall.cartItem.service.CartItemService;
import com.peacemall.common.constant.CartItemMQConstant;
import com.peacemall.common.constant.EsOperataionMQConstant;

import com.peacemall.common.domain.dto.CartMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartItemListener {
    private final CartItemService cartItemService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = CartItemMQConstant.CART_ITEM_QUEUE, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = CartItemMQConstant.CART_ITEM_EXCHANGE, delayed = "true"),
            key = CartItemMQConstant.CART_ITEM_DELETE_ROUTING_KEY
    ))
    public void deleteCartItemByConfigIds(@Payload String message) {
        log.info("接收到删除购物车商品的消息：{}", message);

        if (StrUtil.isBlank(message)) { // 用 Hutool 判断空字符串
            log.error("删除购物车商品的消息为空");
            return;
        }

        try {
            // 解析 JSON
            CartMessageDTO cartMessageDTO = JSONUtil.toBean(message, CartMessageDTO.class);

            // 业务参数校验
            if (CollectionUtils.isEmpty(cartMessageDTO.getConfigIds()) || cartMessageDTO.getUserId() == null) {
                log.error("删除购物车商品的消息参数不合法: {}", cartMessageDTO);
                return;
            }

            // 执行业务逻辑
            boolean res = cartItemService.deleteCartItemByConfigIds(cartMessageDTO.getConfigIds(), cartMessageDTO.getUserId());

            if (!res) {
                log.warn("删除购物车商品失败，触发重试: {}", cartMessageDTO);
                throw new RuntimeException("删除购物车商品失败");
            }

            log.info("删除购物车商品成功, DTO: {}", cartMessageDTO);

        } catch (Exception e) {
            log.error("删除购物车商品异常, 消息内容: {}", message, e);
            throw e; // 抛出异常，Spring RabbitMQ 自动触发重试
        }
    }

}
