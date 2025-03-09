package com.peacemall.shop.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import com.peacemall.shop.enums.ShopStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shops")
public class Shops implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "shop_id", type = IdType.ASSIGN_ID)
    private Long shopId;

    @TableField("user_id")
    private Long userId;

    @TableField("shop_name")
    private String shopName;

    @TableField("shop_status")
    private ShopStatus shopStatus;

    @TableField("shop_description")
    private String shopDescription;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;

    @TableField("shop_avatar_url")
    private String shopAvatarUrl;
}
