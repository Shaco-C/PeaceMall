package com.peachmall.search.controller;

import com.peacemall.common.domain.R;
import com.peachmall.search.service.EsShopOperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es-shop")
@Api(tags = "ES商店操作接口")
@RequiredArgsConstructor
public class EsShopOperationController {

    private final EsShopOperationService esShopOperationService;

    //将mysql中的商店数据,批量导入到es中
    @ApiOperation("将mysql中的商店数据,批量导入到es中")
    @PutMapping("/admin/LoadShopDocs")
    public R<String> loadShopDocs(){
        return esShopOperationService.loadShopDocs();
    }

    //创建shop索引
    @ApiOperation("创建shop索引")
    @PostMapping("/admin/createShopIndex")
    public R<String> createShopIndex(){
        return esShopOperationService.createShopIndex();
    }
}
