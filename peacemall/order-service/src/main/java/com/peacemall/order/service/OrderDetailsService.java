package com.peacemall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.order.domain.po.OrderDetails;

import java.util.List;

public interface OrderDetailsService extends IService<OrderDetails> {

    // 根据订单的Id获取订单的详细信息
    List<OrderDetails> getOrderDetailsByOrderId(Long orderId);


}
