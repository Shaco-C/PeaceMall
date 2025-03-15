package com.peacemall.logs.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.common.enums.WalletFlowType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("flow_logs")
public class FlowLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "wallet_logs_id", type = IdType.ASSIGN_ID)
    private Long walletLogsId;

    @TableField("wallet_id")
    private Long walletId;

    @TableField("user_id")
    private Long userId;

    @TableField("related_order")
    private Long relatedOrder;

    @TableField("flow_type")
    private WalletFlowType flowType;

    @TableField("balance_change")
    private BigDecimal balanceChange;

    @TableField("balance_after")
    private BigDecimal balanceAfter;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;
}
