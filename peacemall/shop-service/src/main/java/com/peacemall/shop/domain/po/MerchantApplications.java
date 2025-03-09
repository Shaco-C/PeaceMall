package com.peacemall.shop.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.shop.enums.ApplicationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("merchant_applications")
public class MerchantApplications implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "application_id", type = IdType.ASSIGN_ID)
    private Long applicationId;

    @TableField("user_id")
    private Long userId;

    @TableField("shop_name")
    private String shopName;

    @TableField("shop_avatar_url")
    private String shopAvatarUrl;

    @TableField("shop_description")
    private String shopDescription;

    @TableField("status")
    private ApplicationStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;

    @TableField("reason")
    private String reason;
}
