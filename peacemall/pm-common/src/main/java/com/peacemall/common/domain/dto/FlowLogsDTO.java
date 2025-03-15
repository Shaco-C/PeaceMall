package com.peacemall.common.domain.dto;


import com.peacemall.common.enums.WalletFlowType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlowLogsDTO {

    private Long walletId;
    private Long userId;

    private Long relatedOrder;

    private WalletFlowType flowType;

    private BigDecimal balanceChange;

    private BigDecimal balanceAfter;

}
