package com.peacemall.favorite.domain.po;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 收藏商品表 (favorites)
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("favorites")
public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "favorites_id", type = IdType.ASSIGN_ID)
    private Long favoritesId;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;
}
