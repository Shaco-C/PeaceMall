package com.peacemall.common.domain.dto;

import lombok.Data;
import java.util.Map;

@Data
public class OrderStockMessageDTO {
    private Long orderId;  // 订单ID
    private Map<Long, Integer> stockChangeMap;  // 该订单影响的库存变更
}
