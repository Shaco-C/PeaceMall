package com.peacemall.favorite.domain.vo;


import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class FavoriteProductVO {
    private Long favoritesId;

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

    public FavoriteProductVO(Long favoritesId,ProductBasicInfosAndShopInfos productBasicInfosAndShopInfos) {
        this.favoritesId = favoritesId;
        this.productId = productBasicInfosAndShopInfos.getProductId();
        this.categoryId = productBasicInfosAndShopInfos.getCategoryId();
        this.categoryName = productBasicInfosAndShopInfos.getCategoryName();
        this.brand = productBasicInfosAndShopInfos.getBrand();
        this.imageUrl = productBasicInfosAndShopInfos.getImageUrl();
        this.name = productBasicInfosAndShopInfos.getName();
        this.description = productBasicInfosAndShopInfos.getDescription();
        this.price = productBasicInfosAndShopInfos.getPrice();
        this.sales = productBasicInfosAndShopInfos.getSales();
        this.updatedAt = productBasicInfosAndShopInfos.getUpdatedAt();
        this.shopId = productBasicInfosAndShopInfos.getShopId();
        this.shopName = productBasicInfosAndShopInfos.getShopName();
        this.shopAvatarUrl = productBasicInfosAndShopInfos.getShopAvatarUrl();
    }
}
