package com.peacemall.wallet.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("withdraw_request")
public class WithdrawRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "request_id", type = IdType.ASSIGN_ID)
    private Long requestId;

    @TableField("user_id")
    private Long userId;

    @TableField("wallet_id")
    private Long walletId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("status")
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED

    @TableField("reason")
    private String reason;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("account_info")
    private String accountInfo;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}
