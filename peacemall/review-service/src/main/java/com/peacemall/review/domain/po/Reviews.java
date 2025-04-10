package com.peacemall.review.domain.po;


import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.review.enums.ReviewStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 评论表 (reviews)
 * 记录用户对商品的评论信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reviews")
public class Reviews implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "review_id", type = IdType.ASSIGN_ID)
    private Long reviewId;  // 评论唯一ID

    @TableField("product_id")
    private Long productId;  // 商品ID，引用商品表

    @TableField("user_id")
    private Long userId;  // 用户ID，引用用户表

    @TableField("parent_review_id")
    private Long parentReviewId;  // 父评论ID

    @TableField("rating")
    private Integer rating;  // 评分（1-5）

    @TableField("comment")
    private String comment;  // 评论内容

    @TableField("status")
    private ReviewStatus status;  // 评论审核状态，默认 APPROVED

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;  // 评论创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;  // 评论更新时间

    @TableField("report_count")
    private Integer reportCount;  // 被举报次数
}
