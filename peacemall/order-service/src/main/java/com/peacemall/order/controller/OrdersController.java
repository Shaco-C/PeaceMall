package com.peacemall.order.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.PurchaseDTO;
import com.peacemall.order.domain.vo.OrderDetailsVO;
import com.peacemall.order.domain.vo.OrdersHistoryVO;
import com.peacemall.order.service.OrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api("订单服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    //创建订单
    @ApiOperation(value = "创建订单")
    @PostMapping("/create")
    public R<String> createOrders(@RequestBody PurchaseDTO purchaseDTO){
        return ordersService.createOrders(purchaseDTO);
    }

    //根据订单id查询订单详情
    @ApiOperation(value = "根据订单id查询订单详情")
    @GetMapping("/getOrderDetailsByOrderId/{orderId}")
    public R<OrderDetailsVO> getOrderDetailsById(@PathVariable Long orderId){
        return ordersService.getOrderDetailsById(orderId);
    }

    //查看历史订单列表
    @ApiOperation(value = "查看历史订单列表")
    @GetMapping("/getOrderHistoryList")
    public R<PageDTO<OrdersHistoryVO>> getOrderHistoryList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return ordersService.getOrderHistoryList(page,pageSize);
    }

    //取消订单
    @ApiOperation(value = "取消订单")
    @PostMapping("/cancelOrder/{orderId}")
    public R<String> cancelOrder(@PathVariable Long orderId){
        return ordersService.cancelOrder(orderId);
    }
}
