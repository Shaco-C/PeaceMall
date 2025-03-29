package com.peacemall.product.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.service.ProductImagesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api("商品图片相关服务")
@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImagesController {

    private final ProductImagesService productImagesService;

    //增加图片
    @ApiOperation("增加图片")
    @PostMapping("/add")
    public R<String> addProductImages(@RequestBody ProductImages productImages){
        return productImagesService.addProductImages(productImages);
    }

    //删除商品图片
    @ApiOperation("删除图片")
    @DeleteMapping("/delete")
    public R<String> deleteProductImages(@RequestBody IdsDTO idsDTO){
        return productImagesService.deleteProductImages(idsDTO.getIdsList());
    }


}
