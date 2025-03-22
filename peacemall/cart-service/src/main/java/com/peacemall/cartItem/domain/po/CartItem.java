package com.peacemall.cartItem.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 购物车详情表 (cart_items)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart_items")
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "cart_item_id", type = IdType.ASSIGN_ID)
    private Long cartItemId;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("config_id")
    private Long configId;

    @TableField("quantity")
    private Integer quantity;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;
}