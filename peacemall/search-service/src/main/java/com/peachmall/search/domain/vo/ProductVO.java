package com.peachmall.search.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "商品搜索结果")
public class ProductVO {
    @ApiModelProperty("商品ID")
    private Long productId;
    @ApiModelProperty("商品ID")
    private Long categoryId;
    @ApiModelProperty("分类名称")
    private String categoryName;
    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("商品描述")
    private String description;
    @ApiModelProperty("销量")
    private Integer sales;

    @ApiModelProperty("图片")
    private String imageUrl;

    public ProductVO(Long productId, Long categoryId, String categoryName, String brand, String name, BigDecimal price, String description, Integer sales, String imageUrl) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.description = description;
        this.sales = sales;
        this.imageUrl = imageUrl;
    }

    public ProductVO() {
    }
}