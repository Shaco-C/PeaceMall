package com.peacemall.logs.listener;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.peacemall.common.constant.EsOperataionMQConstant;

import com.peacemall.common.constant.StockChangeLogMQConstant;
import com.peacemall.common.domain.dto.StockChangeLogDTO;
import com.peacemall.logs.domain.po.StockChangeLog;
import com.peacemall.logs.service.StockChangeLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockChangeLogListener {

    private final StockChangeLogService stockChangeLogService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = StockChangeLogMQConstant.STOCK_LOG_QUEUE, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = EsOperataionMQConstant.ES_OPERATION_DLX_EXCHANGE_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = EsOperataionMQConstant.ES_OPERATION_DLX_ROUTING_KEY)
                    }),
            exchange = @Exchange(name = StockChangeLogMQConstant.STOCK_LOG_EXCHANGE, durable = "true", type = ExchangeTypes.DIRECT),
            key = StockChangeLogMQConstant.STOCK_LOG_ADD_ROUTING_KEY
    ))
    @Transactional(rollbackFor = Exception.class) // 确保事务一致性
    public void addStockChangeLog(@Payload String message) {
        log.info("【库存变化日志】收到库存变化消息: {}", message);

        if (StrUtil.isBlank(message)) {
            log.error("【库存变化日志】收到空消息，丢弃");
            return;
        }

        try {
            // 解析 JSON 为 DTO 列表
            List<StockChangeLogDTO> stockChangeLogDTOS = JSONUtil.toList(message, StockChangeLogDTO.class);
            log.info("【库存变化日志】消息转换成对象: {}", stockChangeLogDTOS);

            if (stockChangeLogDTOS.isEmpty()) {
                log.warn("【库存变化日志】消息为空列表，忽略处理");
                return;
            }

            // 转换为数据库实体
            List<StockChangeLog> stockChangeLogs = BeanUtil.copyToList(stockChangeLogDTOS, StockChangeLog.class);

            // 批量保存
            boolean saved = stockChangeLogService.saveBatch(stockChangeLogs);
            if (!saved) {
                log.error("【库存变化日志】保存失败，数据: {}", stockChangeLogs);
                throw new RuntimeException("批量保存库存变更日志失败");
            }

            log.info("【库存变化日志】库存日志保存成功，共 {} 条", stockChangeLogs.size());
        } catch (Exception e) {
            log.error("【库存变化日志】消息处理失败，异常信息: {}", e.getMessage(), e);
            throw new RuntimeException("库存日志消息处理失败", e);
        }
    }


}
