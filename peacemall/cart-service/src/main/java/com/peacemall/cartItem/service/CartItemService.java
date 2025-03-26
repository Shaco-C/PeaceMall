package com.peacemall.cartItem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.cartItem.domain.dto.CartItemDTO;
import com.peacemall.cartItem.domain.po.CartItem;
import com.peacemall.cartItem.domain.vo.CartItemVO;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;

import java.util.List;

public interface CartItemService extends IService<CartItem> {

    //将商品加入购物车
    R<String> addProductToCart(CartItemDTO cartItemDTO);

    //将商品从购物车中删除
    R<String> deleteProductFromCart(Long cartItemId);

    //修改购物车中商品的数量
    R<String> updateProductQuantity(Long cartItemId, Integer quantity);

    //获取购物车中的商品列表
    R<PageDTO<CartItemVO>> showCartItems(int page,int pageSize);

    //通过configIds将购物车中的内容删除
    boolean deleteCartItemByConfigIds(List<Long> configIds,Long userId);


}
