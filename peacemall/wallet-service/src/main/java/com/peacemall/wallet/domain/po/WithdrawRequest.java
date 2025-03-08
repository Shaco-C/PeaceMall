package com.peacemall.wallet.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.wallet.enums.WithDrawRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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
    private WithDrawRequestStatus status; // PENDING, APPROVED, REJECTED, COMPLETED

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

    public WithdrawRequest(Long userId, Long walletId, BigDecimal amount, WithDrawRequestStatus status, String paymentMethod, String accountInfo) {
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.accountInfo = accountInfo;
    }
}
