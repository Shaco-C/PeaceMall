package com.peacemall.order.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailsProductInfoDTO {
    private Long orderItemId;  // 订单详情ID

    // 商品信息
    private Long productId; //商品id
    private String name; //商品名称
    private String brand; //商品品牌
    private String url; //商品图片

    private Long configId;  // 商品配置ID
    private String configuration; //商品配置详情
    private Integer quantity;  // 购买数量
    private BigDecimal price;  // 商品单价
}
