package com.peacemall.product.domain.po;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 * 商品配置及库存表 (product_configurations)
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_configurations")
public class ProductConfigurations implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "config_id", type = IdType.ASSIGN_ID)
    private Long configId;

    @TableField("product_id")
    private Long productId;

    @TableField("configuration")
    private String configuration;

    @TableField("price")
    private BigDecimal price;

    @TableField("stock")
    private Integer stock;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}