package com.peacemall.common.domain.dto;



import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletAmountChangeDTO {
    //可能是用户id或者是商店id
    private Long id;


    private BigDecimal changeAmount;

}
