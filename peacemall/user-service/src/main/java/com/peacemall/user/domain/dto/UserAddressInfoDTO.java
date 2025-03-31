package com.peacemall.user.domain.dto;



import lombok.Data;



@Data
public class UserAddressInfoDTO {

    //地址id
    private Long addressId;

    //用户id
    private Long userId;

    //收货人姓名
    private String consignee;

    //收货人手机号
    private String phone;

    //收货人国家
    private String country;

    //收货人省份
    private String province;

    //收货人城市
    private String city;

    //收货人区县
    private String district;

    //收货人详细地址
    private String street;

    /**
     * 是否默认地址，0-否，1-是
     */

    private Boolean isDefault;


    //地址标签信息(家、公司等等)
    private String addressTag;

    /**
     * 状态（1-有效，0-无效）
     */

    private Integer status;

}
