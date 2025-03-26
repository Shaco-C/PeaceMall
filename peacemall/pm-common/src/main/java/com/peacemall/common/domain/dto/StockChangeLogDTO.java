package com.peacemall.common.domain.dto;

import com.peacemall.common.enums.StockSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockChangeLogDTO {

    private Long productId;

    private Long configId;

    private Integer delta;

    private StockSourceType sourceType;

}
