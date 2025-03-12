package com.peacemall.product.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author watergun
 */
@Data
public class ProductConfigDTO {

    @NotNull(message = "产品id不能为空")
    private Long productId;

    @NotNull(message = "产品配置不能为空")
    private String configuration;

    @NotNull(message = "产品价格不能为空")
    private BigDecimal price;

    @NotNull(message = "产品库存不能为空")
    private Integer stock;

}
