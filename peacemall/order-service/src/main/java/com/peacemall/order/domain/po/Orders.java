package com.peacemall.order.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.enums.ReturnStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 订单表 (orders)
 * 记录用户的订单信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders")
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private Long orderId;  // 订单ID

    @TableField("user_id")
    private Long userId;  // 用户ID

    @TableField("shop_id")
    private Long shopId;  // 商家ID

    @TableField("address_id")
    private Long addressId;  // 收件地址ID

    @TableField("logistics_number")
    private String logisticsNumber;  // 物流单号

    @TableField("logistics_com")
    private String logisticsCom;  // 物流公司名称

    @TableField("total_amount")
    private BigDecimal totalAmount;  // 商品实付金额

    @TableField("original_amount")
    private BigDecimal originalAmount;  // 商品总金额

    @TableField("status")
    private OrderStatus status;  // 订单状态

    @TableField("return_status")
    private ReturnStatus returnStatus;  // 退货状态

    @TableField("payment_type")
    private Integer paymentType;  // 支付方式 1-支付宝 2-微信 3-扣减余额

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;  // 订单创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;  // 订单更新时间

    @TableField("consign_time")
    private Timestamp consignTime;  // 发货时间

    @TableField("end_time")
    private Timestamp endTime;  // 交易完成时间
}
