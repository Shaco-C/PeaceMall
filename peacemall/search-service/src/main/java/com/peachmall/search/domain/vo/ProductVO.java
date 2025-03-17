package com.peachmall.search.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty("商品描述")
    private String description;
    @ApiModelProperty("销量")
    private Integer sales;
}