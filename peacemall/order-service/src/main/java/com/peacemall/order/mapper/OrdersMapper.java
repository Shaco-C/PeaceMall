package com.peacemall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.order.domain.po.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
