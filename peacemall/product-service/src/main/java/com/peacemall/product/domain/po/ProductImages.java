package com.peacemall.product.domain.po;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
/**
 * 商品多图支持 (product_images)
 * @author watergun
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_images")
public class ProductImages implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "image_id", type = IdType.ASSIGN_ID)
    private Long imageId;

    @TableField("product_id")
    private Long productId;

    @TableField("url")
    private String url;

    @TableField("is_main")
    private Boolean isMain;

    @TableField("sort_order")
    private Integer sortOrder;
}
