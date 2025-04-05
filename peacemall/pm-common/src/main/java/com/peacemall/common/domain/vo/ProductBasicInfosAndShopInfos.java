package com.peacemall.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBasicInfosAndShopInfos {

    private Long productId;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String imageUrl;
    private String name;
    private String description;
    private BigDecimal price; // 取该商品配置中的最小价格
    private Integer sales;
    private Timestamp updatedAt;

    private Long shopId;
    private String shopName;
    private String shopAvatarUrl;

}
