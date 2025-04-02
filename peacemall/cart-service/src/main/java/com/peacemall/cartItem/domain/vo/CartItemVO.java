package com.peacemall.cartItem.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("购物车列表商品信息")
public class CartItemVO {
    @ApiModelProperty("购物车商品id")
    private Long cartItemId;

    //商品信息
    @ApiModelProperty("商品id")
    private Long productId;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品品牌")
    private String brand;

    //商品图片信息
    @ApiModelProperty("商品图片")
    private String url;

    //商品详细信息
    @ApiModelProperty("商品配置id")
    private Long configId;
    @ApiModelProperty("商品配置信息")
    private String configuration;
    @ApiModelProperty("商品价格")
    private BigDecimal price;
    @ApiModelProperty("商品数量")
    private Integer quantity;

    //商家信息
    @ApiModelProperty("商家id")
    private Long shopId;
    @ApiModelProperty("商家名称")
    private String shopName;

}
