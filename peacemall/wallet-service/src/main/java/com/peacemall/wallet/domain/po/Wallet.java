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
@TableName("wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "wallet_id", type = IdType.ASSIGN_ID)
    private Long walletId;

    @TableField("user_id")
    private Long userId;

    @TableField("total_balance")
    private BigDecimal totalBalance;

    @TableField("available_balance")
    private BigDecimal availableBalance;

    @TableField("pending_balance")
    private BigDecimal pendingBalance;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}
