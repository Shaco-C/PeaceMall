package com.peacemall.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    private Long categoryId;
    private String categoryName;
    private String brand;
    private String imageUrl;
    private String name;
    private String description;

    private BigDecimal price; // 取该商品配置中的最小价格

    private Integer sales;
    private Timestamp updatedAt;

}
