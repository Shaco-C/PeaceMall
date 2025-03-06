package com.peacemall.user.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "信息验证")
public class VerifyInfosDTO {

    @ApiModelProperty(value = "现在的信息")
    String currentInfo;

    @ApiModelProperty(value = "要修改的信息")
    String newInfo;

}
