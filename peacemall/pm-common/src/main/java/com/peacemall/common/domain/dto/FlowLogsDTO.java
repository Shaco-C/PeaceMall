package com.peacemall.common.domain.dto;


import com.peacemall.common.enums.WalletFlowType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowLogsDTO {

    private Long walletId;
    private Long userId;

    private Long relatedOrder;

    private WalletFlowType flowType;

    private BigDecimal balanceChange;

    private BigDecimal balanceAfter;

}
