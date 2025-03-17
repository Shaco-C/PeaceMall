package com.peachmall.search.controller;


import com.peacemall.common.domain.R;
import com.peachmall.search.service.EsOperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es")
@Api(tags = "ES操作接口")
@RequiredArgsConstructor
public class EsOperationController{

    private final EsOperationService esOperationService;

    @ApiOperation("将mysql数据库中的数据全部插入到es中")
    @PutMapping("/admin/LoadProductDocs")
    public R<String> loadProductDocs(){
        return esOperationService.loadProductDocs();
    }

    //将mysql中的用户数据,批量导入到es中
    @ApiOperation("将mysql中的用户数据,批量导入到es中")
    @PutMapping("/admin/LoadUserDocs")
    public R<String> loadUserDocs(){
        return esOperationService.loadUserDocs();
    }

    //将mysql中的商店数据,批量导入到es中
    @ApiOperation("将mysql中的商店数据,批量导入到es中")
    @PutMapping("/admin/LoadShopDocs")
    public R<String> loadShopDocs(){
        return esOperationService.loadShopDocs();
    }
    @ApiOperation("将mysql数据库中的数据全部插入到es中")
    @PostMapping("/admin/createProductIndex")
    public R<String> createProductIndex(){
        return esOperationService.createProductIndex();
    }


    //创建user索引
    @ApiOperation("创建user索引")
    @PostMapping("/admin/createUserIndex")
    public R<String> createUserIndex(){
        return esOperationService.createUserIndex();
    }

    //创建shop索引
    @ApiOperation("创建shop索引")
    @PostMapping("/admin/createShopIndex")
    public R<String> createShopIndex(){
        return esOperationService.createShopIndex();
    }
}
