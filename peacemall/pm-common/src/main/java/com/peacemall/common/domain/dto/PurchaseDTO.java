package com.peacemall.common.domain.dto;


import lombok.Data;

import java.util.List;


/**
 * 用于用户购买商品之后
 * 传入相应的配置扣减信息
 */
@Data
public class PurchaseDTO {
    private Long userId;  // 用户ID
    private List<PurchaseItem> items;  // 购买商品列表
    private Long addressId;  // 收货地址ID
    private Integer paymentType;// 支付方式
    private Long couponId;  // 优惠券ID
}
