package com.peachmall.search.controller;


import com.peacemall.common.domain.R;
import com.peachmall.search.service.EsUserOperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es-user")
@Api(tags = "ES用户操作接口")
@RequiredArgsConstructor
public class EsUserOperationController {

    private final EsUserOperationService esUserOperationService;

    //将mysql中的用户数据,批量导入到es中
    @ApiOperation("将mysql中的用户数据,批量导入到es中")
    @PutMapping("/admin/LoadUserDocs")
    public R<String> loadUserDocs(){
        return esUserOperationService.loadUserDocs();
    }

    //创建user索引
    @ApiOperation("创建user索引")
    @PostMapping("/admin/createUserIndex")
    public R<String> createUserIndex(){
        return esUserOperationService.createUserIndex();
    }

}
