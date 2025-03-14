package com.peacemall.common.domain.vo;

import lombok.Data;

@Data
public class ProductBasicInfosAndShopInfos {
    private Long productId;
    private Long shopId;
    private String shopName;
    private String shopDescription;
    private String shopAvatarUrl;
    private String brand;
    private String name;
    private String description;
    private Long categoryId;
    private Long sales;
}
