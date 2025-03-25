package com.peacemall.common.domain.dto;


import lombok.Data;

/**
 * 购买商品的详情
 */
@Data
public class PurchaseItem {
    private Long productId;  // 商品ID
    private Long configId;  // 商品配置ID
    private Integer quantity;  // 购买数量
}