package com.peacemall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.PurchaseDTO;
import com.peacemall.order.domain.po.Orders;
import com.peacemall.order.domain.vo.OrderDetailsVO;
import com.peacemall.order.domain.vo.OrdersHistoryVO;
import com.peacemall.order.enums.ReturnStatus;

public interface OrdersService extends IService<Orders> {

    //创建订单
    R<String> createOrders(PurchaseDTO purchaseDTO);

    //根据Id获取订单详情
    R<OrderDetailsVO> getOrderDetailsById(Long orderId);

    //查看历史订单列表
    R<PageDTO<OrdersHistoryVO>> getOrderHistoryList(int page,int pageSize);

    //取消订单
    R<String> cancelOrder(Long orderId);

    //支付订单
    R<String> payOrder(Long orderId);

    //删除订单
    R<String> deleteOrder(Long orderId);

    //用户申请退货
    R<String> userApplyForReturn(Long orderId);

    //商家审核退货申请
    R<String> merchantAuditReturnApplication(Long orderId, ReturnStatus returnStatus);

    //商家发货

    //商家分页查询自己店铺的所有订单,包括特定的信息


    //用户按照原地址退货

    //用户确认收货


}
