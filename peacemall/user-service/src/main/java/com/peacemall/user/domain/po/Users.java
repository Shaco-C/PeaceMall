package com.peacemall.user.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.peacemall.user.enums.UserRole;
import com.peacemall.user.enums.UserState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("password")
    private String password;

    @TableField("phone_number")
    private String phoneNumber;

    @TableField("role")
    private UserRole role;

    @TableField("status")
    private UserState status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Timestamp createdAt;

    @TableField("last_login")
    private Timestamp lastLogin;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("signature")
    private String signature;
}
