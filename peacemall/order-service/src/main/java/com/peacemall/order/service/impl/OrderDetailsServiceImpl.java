package com.peacemall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.order.domain.po.OrderDetails;
import com.peacemall.order.mapper.OrderDetailsMapper;
import com.peacemall.order.service.OrderDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements OrderDetailsService {

    @Override
    public List<OrderDetails> getOrderDetailsByOrderId(Long orderId) {
        log.info("getOrderDetailsByOrderId: orderId={}", orderId);
        if (orderId == null){
            log.error("getOrderDetailsByOrderId: orderId is null");
            return null;
        }
        LambdaQueryWrapper<OrderDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetails::getOrderId, orderId);
        return this.list(queryWrapper);
    }
}
