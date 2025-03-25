package com.peacemall.order.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PurchaseDTO;
import com.peacemall.order.service.OrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
