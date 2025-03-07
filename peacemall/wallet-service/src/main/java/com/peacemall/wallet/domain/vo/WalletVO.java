package com.peacemall.wallet.domain.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author watergun
 */
@Data
public class WalletVO {
    private Long walletId;

    private Long userId;

    private BigDecimal totalBalance;

    private BigDecimal availableBalance;

    private BigDecimal pendingBalance;
}
