package com.peacemall.order.domain.vo;

import com.peacemall.common.domain.dto.UserAddressDTO;
import com.peacemall.order.domain.dto.OrderDetailsProductInfoDTO;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.enums.ReturnStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 订单详情页面所需要的信息
 * @author watergun
 **/

@Data
public class OrderDetailsVO {

    //订单基本信息
    private Long orderId;  // 订单ID
    private Long userId;  // 用户ID
    private String logisticsNumber;  // 物流单号
    private String logisticsCom;  // 物流公司名称
    private BigDecimal totalAmount;  // 商品实付金额
    private BigDecimal originalAmount;  // 商品总金额
    private OrderStatus status;  // 订单状态
    private ReturnStatus returnStatus;  // 退货状态
    private Integer paymentType;  // 支付方式 1-支付宝 2-微信 3-扣减余额
    private Timestamp createdAt;  // 订单创建时间
    private Timestamp updatedAt;  // 订单更新时间
    private Timestamp consignTime;  // 发货时间
    private Timestamp endTime;  // 交易完成时间

    // 地址信息
    private UserAddressDTO userAddressDTO;

    // 商家信息
    private Long shopId;  // 商家ID
    private String shopName;

    //订单详情商品信息
    private List<OrderDetailsProductInfoDTO> orderItemsList;


}
