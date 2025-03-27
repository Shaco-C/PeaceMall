package com.peacemall.order.domain.vo;

import com.peacemall.common.domain.dto.UserAddressDTO;
import com.peacemall.order.domain.dto.OrderDetailsProductInfoDTO;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.enums.ReturnStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersHistoryVO {
    //订单基本信息
    private Long orderId;  // 订单ID
    private Long userId;  // 用户ID
    private BigDecimal totalAmount;  // 商品实付金额
    private BigDecimal originalAmount;  // 商品总金额
    private OrderStatus status;  // 订单状态
    private ReturnStatus returnStatus;  // 退货状态

    // 商家信息
    private Long shopId;  // 商家ID
    private String shopName;

    //订单详情商品信息
    private List<OrderDetailsProductInfoDTO> orderItemsList;

}
