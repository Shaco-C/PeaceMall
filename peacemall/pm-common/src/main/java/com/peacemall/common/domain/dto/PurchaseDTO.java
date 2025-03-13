package com.peacemall.common.domain.dto;


import lombok.Data;

/**
 * 用于用户购买商品之后
 * 传入相应的配置扣减信息
 */
@Data
public class PurchaseDTO {
    private Long configId;
    private Integer quantity;
}
