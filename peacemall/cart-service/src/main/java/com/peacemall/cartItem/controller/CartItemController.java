package com.peacemall.cartItem.controller;


import com.peacemall.cartItem.domain.dto.CartItemDTO;
import com.peacemall.cartItem.domain.vo.CartItemVO;
import com.peacemall.cartItem.service.CartItemService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api("购物车服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cartItem")
public class CartItemController {
    private final CartItemService cartItemService;

    //将商品加入购物车
    @ApiOperation("将商品加入购物车")
    @PostMapping("/addProductToCart")
    public R<String> addProductToCart(@RequestBody CartItemDTO cartItemDTO){
        return cartItemService.addProductToCart(cartItemDTO);
    }

    //批量删除购物车中的商品
    @ApiOperation("批量删除购物车中的商品")
    @DeleteMapping("/deleteProductFromCartBatch")
    public R<String> deleteProductFromCartBatch(@RequestBody List<Long> cartItemIds){
        return cartItemService.deleteProductFromCartBatch(cartItemIds);
    }

    //修改购物车中商品的数量
    @ApiOperation("修改购物车中商品的数量")
    @PutMapping("/updateProductQuantity")
    public R<String> updateProductQuantity(@RequestBody List<CartItemDTO> cartItemDTOList){
        return cartItemService.updateProductQuantity(cartItemDTOList);
    }

    //获取购物车中的商品列表
    @ApiOperation("获取购物车中的商品列表")
    @GetMapping("/showCartItems")
    public R<PageDTO<CartItemVO>> showCartItems(@RequestParam(value = "page",defaultValue = "1") int page,
                                         @RequestParam(value = "pageSize",defaultValue = "20") int pageSize){
        return cartItemService.showCartItems(page, pageSize);
    }
}
