package com.peacemall.common.domain.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private Long addressId;

    private Long userId;

    private String consignee;

    private String phone;

    private String country;

    private String province;

    private String city;

    private String district;

    private String street;
    private String addressTag;
}
