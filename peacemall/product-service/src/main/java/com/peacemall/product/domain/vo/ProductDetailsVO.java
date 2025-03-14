package com.peacemall.product.domain.vo;

import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.enums.ProductStatus;
import com.peacemall.product.enums.ProductStockMode;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ProductDetailsVO {
    private Long productId;

    private Long shopId;
    //商家的信息
    private String shopName;
    private String shopDescription;
    private String shopAvatarUrl;

    private Long userId;

    private String brand;

    private String name;

    private String description;

    private ProductStockMode stockMode;

    private Boolean isActive;

    private ProductStatus status;

    private Long categoryId;

    private Integer sales;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private List<ProductConfigurations> productConfigurationsList;

    private List<ProductImages> productImagesList;
}
