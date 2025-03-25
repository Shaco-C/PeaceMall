package com.peacemall.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("商品配置详情")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductConfigurationDTO {

    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("商品配置id")
    private Long configId;

    @ApiModelProperty("商品配置信息")
    private String configuration;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("商品库存")
    private Integer stock;
}
