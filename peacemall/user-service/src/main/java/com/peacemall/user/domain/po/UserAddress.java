package com.peacemall.user.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_address")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "address_id", type = IdType.ASSIGN_ID)
    private Long addressId;

    @TableField("user_id")
    private Long userId;

    @TableField("consignee")
    private String consignee;

    @TableField("phone")
    private String phone;

    @TableField("country")
    private String country;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("district")
    private String district;

    @TableField("street")
    private String street;

    /**
     * 是否默认地址，0-否，1-是
     */
    @TableField("is_default")
    private Boolean isDefault;

    @TableField("address_tag")
    private String addressTag;

    /**
     * 状态（1-有效，0-无效）
     */
    @TableField("status")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}
