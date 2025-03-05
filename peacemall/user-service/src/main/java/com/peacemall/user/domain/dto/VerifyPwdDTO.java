package com.peacemall.user.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "密码传输实体")
public class VerifyPwdDTO {
    @ApiModelProperty(value = "新密码")
    String newPassword;
    @ApiModelProperty(value = "现在的密码")
    String currentPassword;
}
