package com.peacemall.order.listener;

import cn.hutool.json.JSONUtil;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.constant.OrderListenerMQConstant;
import com.peacemall.common.domain.dto.OrderStockMessageDTO;
import com.peacemall.order.domain.po.Orders;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {
    private final OrdersService ordersService;
    private final ProductClient productClient;

    /**
     * 检查订单支付状态,未支付则取消订单并且库存回滚
     * @param message OrderStockMessageDTO的Json格式信息
     * @author watergun
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = OrderListenerMQConstant.ORDER_DELAY_PAYMENT_STATUS_QUEUE, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = OrderListenerMQConstant.ORDER_DELAY_DIRECT_EXCHANGE,delayed = "true"),
            key = OrderListenerMQConstant.ORDER_DELAY_PAYMENT_STATUS_CHECK_ROUTING_KEY
    ))
    public void checkOrderPaymentStatus(String message) {
        log.info("checkOrderPaymentStatus: received message: {}", message);

        // 转换消息
        OrderStockMessageDTO orderStockMessageDTO = JSONUtil.toBean(message, OrderStockMessageDTO.class);

        // 获取订单信息
        Orders orders = ordersService.getById(orderStockMessageDTO.getOrderId());
        if (orders == null) {
            log.error("checkOrderPaymentStatus: Order ID {} not found", orderStockMessageDTO.getOrderId());
            return;
        }

        // 仅处理待支付状态的订单
        if (!OrderStatus.PENDING_PAYMENT.equals(orders.getStatus())) {
            log.info("checkOrderPaymentStatus: Order {} is already processed, skipping", orders.getOrderId());
            return;
        }

        // 更新订单状态为已取消
        orders.setStatus(OrderStatus.CANCELLED);
        if (!ordersService.updateById(orders)) {
            log.error("checkOrderPaymentStatus: Failed to update order status for ID {}", orders.getOrderId());
            throw new RuntimeException("订单取消失败");
        }
        log.info("checkOrderPaymentStatus: Order {} cancelled successfully", orders.getOrderId());

        // 计算库存回滚信息（变更数取反）
        Map<Long, Integer> rollbackStockMap = orderStockMessageDTO.getStockChangeMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> -entry.getValue()));

        // 调用商品服务回滚库存
        productClient.updateProductConfigurationsQuantity(rollbackStockMap);
        log.info("checkOrderPaymentStatus: Rolled back stock {}", rollbackStockMap);
    }

}
