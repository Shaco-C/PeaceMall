package com.peacemall.order.controller;


import com.peacemall.order.service.OrderDetailsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("订单详情服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    private final OrderDetailsService orderDetailsService;

}
