package com.peacemall.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer sales;
}
