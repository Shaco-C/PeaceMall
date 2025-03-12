package com.peacemall.product.domain.dto;

import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.enums.ProductStockMode;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddProductDTO {

    //该商品的商店id
    @NotNull(message="商店id不能为空")
    private Long shopId;

    //该商品的品牌
    private String brand;

    //该商品的名称
    @NotNull(message="商品名称不能为空")
    private String name;

    //该商品的介绍
    private String description;

    //预售商品还是现货商品
    @NotNull(message="商品状态不能为空")
    private ProductStockMode productStockMode;

    //分类id
    @NotNull(message="分类不能为空")
    private Long categoryId;

    //商品图片
    @NotNull(message="商品图片不能为空")
    List<ProductImages> productImages;

}
