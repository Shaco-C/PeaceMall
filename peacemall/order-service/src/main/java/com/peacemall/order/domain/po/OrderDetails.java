package com.peacemall.order.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情表 (order_details)
 * 记录订单的商品明细
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_details")
public class OrderDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_item_id", type = IdType.ASSIGN_ID)
    private Long orderItemId;  // 订单详情ID

    @TableField("order_id")
    private Long orderId;  // 订单ID

    @TableField("product_id")
    private Long productId;  // 商品ID

    @TableField("config_id")
    private Long configId;  // 商品配置ID

    @TableField("quantity")
    private Integer quantity;  // 购买数量

    @TableField("price")
    private BigDecimal price;  // 商品单价
}
