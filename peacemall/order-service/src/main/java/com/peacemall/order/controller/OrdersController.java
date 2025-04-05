package com.peacemall.order.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.PurchaseDTO;
import com.peacemall.order.domain.vo.OrderDetailsVO;
import com.peacemall.order.domain.vo.OrdersHistoryVO;
import com.peacemall.order.enums.ReturnStatus;
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
    @PutMapping("/cancelOrder/{orderId}")
    public R<String> cancelOrder(@PathVariable Long orderId){
        return ordersService.cancelOrder(orderId);
    }

    //确认收货
    @ApiOperation(value = "确认收货")
    @PutMapping("/userConfirmReceipt/{orderId}")
    public R<String> userConfirmReceipt(@PathVariable Long orderId){
        return ordersService.userConfirmReceipt(orderId);
    }
    //支付订单
    @ApiOperation(value = "支付订单")
    @PutMapping("/payOrder/{orderId}")
    public R<String> payOrder(@PathVariable("orderId") Long orderId){
        return ordersService.payOrder(orderId);
    }

    //删除订单
    @ApiOperation(value = "删除订单")
    @DeleteMapping("/deleteOrder/{orderId}")
    public R<String> deleteOrder(@PathVariable("orderId") Long orderId){
        return ordersService.deleteOrder(orderId);
    }

    //用户申请退货
    @ApiOperation(value = "用户申请退货")
    @PutMapping("/userApplyForReturn/{orderId}")
    public R<String> userApplyForReturn(@PathVariable("orderId") Long orderId){
        return ordersService.userApplyForReturn(orderId);
    }

    //商家审核退货申请
    @ApiOperation(value = "商家审核退货申请")
    @PutMapping("/merchant/merchantAuditReturnApplication")
    public R<String> merchantAuditReturnApplication(@RequestParam("orderId")Long orderId,
                                                    @RequestParam("returnStatus")ReturnStatus returnStatus){
        return ordersService.merchantAuditReturnApplication(orderId,returnStatus);
    }
}
