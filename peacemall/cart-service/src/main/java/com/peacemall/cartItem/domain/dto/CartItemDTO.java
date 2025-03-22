package com.peacemall.cartItem.domain.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long productId;
    private Long configId;
    private Integer quantity;
}
