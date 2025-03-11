package com.peacemall.product.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.product.enums.ProductStatus;
import com.peacemall.product.enums.ProductStockMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * 商品基本信息表 (products)
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("products")
public class Products implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "product_id", type = IdType.ASSIGN_ID)
    private Long productId;

    @TableField("shop_id")
    private Long shopId;

    @TableField("brand")
    private String brand;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("stock_mode")
    private ProductStockMode stockMode;

    @TableField("is_active")
    private Boolean isActive;

    @TableField("status")
    private ProductStatus status;

    @TableField("category_id")
    private Long categoryId;

    @TableField("sales")
    private Integer sales;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}