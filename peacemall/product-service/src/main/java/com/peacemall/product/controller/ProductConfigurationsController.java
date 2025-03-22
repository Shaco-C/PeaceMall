package com.peacemall.product.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.product.domain.dto.ProductConfigDTO;
import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.service.ProductConfigurationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author watergun
 */
@Api("商品配置服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/productsConfigurations")
public class ProductConfigurationsController {
    private final ProductConfigurationsService productConfigurationsService;

    //创建商品配置
    @ApiOperation("创建商品配置")
    @PostMapping("/merchant/create")
    R<String> merchantCreateProductConfigurations(@RequestBody ProductConfigDTO productConfigDTO){
        return productConfigurationsService.merchantCreateProductConfigurations(productConfigDTO);
    }

    //删除商品配置
    @ApiOperation("删除商品配置")
    @DeleteMapping("/merchant/delete")
    R<String> merchantDeleteProductConfigurations(@RequestBody IdsDTO idsDTO){
        return productConfigurationsService.merchantDeleteProductConfigurations(idsDTO.getIdsList());
    }

    //修改商品配置
    @ApiOperation("修改商品配置")
    @PutMapping("/merchant/update")
    R<String> merchantUpdateProductConfigurations(@RequestBody ProductConfigurations productConfigurations){
        return productConfigurationsService.merchantUpdateProductConfigurations(productConfigurations);
    }

    //通过商品ids获得商品配置


}
