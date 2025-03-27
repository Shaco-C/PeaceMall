package com.peacemall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PurchaseDTO;
import com.peacemall.order.domain.po.Orders;
import com.peacemall.order.domain.vo.OrderDetailsVO;

public interface OrdersService extends IService<Orders> {

    //创建订单
    R<String> createOrders(PurchaseDTO purchaseDTO);

    //根据Id获取订单详情
    R<OrderDetailsVO> getOrderDetailsById(Long orderId);
    //查看历史订单列表

    //取消订单


}
