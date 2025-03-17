package com.peacemall.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {
    private Long shopId;

    private Long userId;

    private String shopName;

    private String shopDescription;

    private Date updatedAt;

    private String shopAvatarUrl;
}
