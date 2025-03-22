package com.peacemall.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("商品详细信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsDTO {

    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品品牌")
    private String brand;

    // 商品图片信息
    @ApiModelProperty("商品主图")
    private String url;

    // 商品所有配置（一个商品可能有多个配置）
    @ApiModelProperty("商品的所有配置列表")
    private List<ProductConfigurationDTO> configurations;

    // 商家信息
    @ApiModelProperty("商家id")
    private Long shopId;

    @ApiModelProperty("商家名称")
    private String shopName;
}
