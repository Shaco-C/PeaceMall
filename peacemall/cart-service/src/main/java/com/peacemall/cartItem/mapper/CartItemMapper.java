package com.peacemall.cartItem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.cartItem.domain.po.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
