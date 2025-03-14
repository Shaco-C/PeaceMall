package com.peacemall.logs.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_change_logs")
public class StockChangeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    @TableField("config_id")
    private Long configId;

    @TableField("delta")
    private Integer delta;

    @TableField("source_type")
    private String sourceType;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;
}
