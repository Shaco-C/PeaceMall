package com.peacemall.wallet.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author watergun
 */

@Data
public class WithdrawInfoVO {
    private BigDecimal amount;
    private String payMethod;
    private String accountInfo;
}
