package com.peacemall.common.domain.vo;

import lombok.Data;

@Data
public class ShopsInfoVO {
    private Long shopId;
    private Long userId;
    private String shopName;
    private String shopDescription;
    private String shopAvatarUrl;
}
