package com.peacemall.product.domain.vo;

import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.enums.ProductStatus;
import com.peacemall.product.enums.ProductStockMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel(description = "商品详情")
public class ProductDetailsVO {
    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "商家id")
    private Long shopId;
    //商家的信息
    @ApiModelProperty(value = "商家名称")
    private String shopName;
    @ApiModelProperty(value = "商家描述")
    private String shopDescription;
    @ApiModelProperty(value = "商家头像")
    private String shopAvatarUrl;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品描述")
    private String description;

    @ApiModelProperty(value = "出售模式")
    private ProductStockMode stockMode;

    @ApiModelProperty(value = "是否上架")
    private Boolean isActive;

    @ApiModelProperty(value = "商品状态")
    private ProductStatus status;

    @ApiModelProperty(value = "商品分类id")
    private Long categoryId;

    @ApiModelProperty(value = "商品销量")
    private Integer sales;

    @ApiModelProperty("创建时间")
    private Timestamp createdAt;

    @ApiModelProperty("更新时间")
    private Timestamp updatedAt;

    @ApiModelProperty(value = "商品配置")
    private List<ProductConfigurations> productConfigurationsList;

    @ApiModelProperty(value = "商品图片")
    private List<ProductImages> productImagesList;
}
