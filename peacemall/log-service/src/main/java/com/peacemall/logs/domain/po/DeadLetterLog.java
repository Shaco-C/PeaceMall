package com.peacemall.logs.domain.po;
import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.logs.enums.DeadLetterLogStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dead_letter_log")
public class DeadLetterLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "dead_letter_log_id", type = IdType.ASSIGN_ID)
    private Long deadLetterLogId; // 主键ID

    @TableField("message")
    private String message; // 失败的日志内容

    @TableField("reason")
    private String reason; // 失败原因

    @TableField("status")
    private DeadLetterLogStatus status; // 处理状态: PENDING, RESOLVED, FAILED

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt; // 创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt; // 更新时间
}
