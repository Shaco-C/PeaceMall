package com.peachmall.search.controller;


import com.peacemall.common.domain.R;
import com.peachmall.search.service.EsProductOperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es-product")
@Api(tags = "ES商品操作接口")
@RequiredArgsConstructor
public class EsProductOperationController {

    private final EsProductOperationService esProductOperationService;

    @ApiOperation("将mysql数据库中的数据全部插入到es中")
    @PutMapping("/admin/LoadProductDocs")
    public R<String> loadProductDocs(){
        return esProductOperationService.loadProductDocs();
    }


    @ApiOperation("将mysql数据库中的数据全部插入到es中")
    @PostMapping("/admin/createProductIndex")
    public R<String> createProductIndex(){
        return esProductOperationService.createProductIndex();
    }
}
