package com.peacemall.favorite.domain.vo;


import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoriteProductVO {
    private Long favoritesId;
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

    public FavoriteProductVO(Long favoritesId, ProductBasicInfosAndShopInfos productBasicInfosAndShopInfos) {
        this.favoritesId = favoritesId;
        this.productId = productBasicInfosAndShopInfos.getProductId();
        this.shopId = productBasicInfosAndShopInfos.getShopId();
        this.shopName = productBasicInfosAndShopInfos.getShopName();
        this.shopDescription = productBasicInfosAndShopInfos.getShopDescription();
        this.shopAvatarUrl = productBasicInfosAndShopInfos.getShopAvatarUrl();
        this.brand = productBasicInfosAndShopInfos.getBrand();
        this.name = productBasicInfosAndShopInfos.getName();
        this.description = productBasicInfosAndShopInfos.getDescription();
        this.categoryId = productBasicInfosAndShopInfos.getCategoryId();
        this.sales = productBasicInfosAndShopInfos.getSales();
    }
}
