package com.peacemall.product.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 商品类别表 (categories)
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("categories")
public class Categories implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "category_id", type = IdType.ASSIGN_ID)
    private Long categoryId;

    @TableField("category_name")
    private String categoryName;

    @TableField("parent_id")
    private Long parentId;

    @TableField("icon")
    private String icon;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}