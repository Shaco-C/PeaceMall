package com.peacemall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.order.domain.po.OrderDetails;
import com.peacemall.order.mapper.OrderDetailsMapper;
import com.peacemall.order.service.OrderDetailsService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements OrderDetailsService {

}
