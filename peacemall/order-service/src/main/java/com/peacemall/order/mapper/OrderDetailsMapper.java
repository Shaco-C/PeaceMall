package com.peacemall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.order.domain.po.OrderDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailsMapper extends BaseMapper<OrderDetails> {
}
