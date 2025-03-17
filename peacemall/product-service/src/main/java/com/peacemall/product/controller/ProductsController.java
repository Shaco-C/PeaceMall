package com.peacemall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import com.peacemall.product.domain.dto.AddProductDTO;
import com.peacemall.product.domain.po.Products;
import com.peacemall.product.domain.vo.ProductDetailsVO;
import com.peacemall.product.enums.ProductStatus;
import com.peacemall.product.service.ProductsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author watergun
 */
@Api("商品服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {
    private final ProductsService productsService;

    //创建商品
    @ApiOperation(value = "创建商品")
    @PostMapping("/merchant/create")
    public R<Long> merchantCreateProduct(@RequestBody @Validated AddProductDTO addProductDTO){
        return productsService.merchantCreateProduct(addProductDTO);
    }

    //商家修改商品上下架状态
    //传递目前的status,后台直接保存!activeStatus
    @ApiOperation(value = "修改商品上下架状态")
    @PutMapping("/merchant/updateActiveStatus")
    public R<String> merchantUpdateProductActiveStatus(@RequestParam("productId") Long productId,
                                                       @RequestParam("activeStatus") Boolean activeStatus){
        return productsService.merchantUpdateProductActiveStatus(productId,activeStatus);
    }

    //商家删除商品
    @ApiOperation(value = "商家删除商品")
    @DeleteMapping("/merchant/delete/{productId}")
    public R<String> merchantDeleteProduct(@PathVariable Long productId){
        return productsService.merchantDeleteProduct(productId);
    }

    //todo 搜索商品 es

    //todo 首页商品显示


    //分页查询查看某个分类下的产品
    @ApiOperation(value = "分页查询查看某个分类下的产品")
    @GetMapping("/getProductsByCategoryId")

    R<PageDTO<Products>> getProductsByCategoryId(@RequestParam(value = "page",defaultValue = "1") int page,
                                                 @RequestParam(value = "pageSize",defaultValue = "20")int pageSize,
                                                 @RequestParam("categoryId") Long categoryId){
        return productsService.getProductsByCategoryId(page,pageSize,categoryId);
    }

    //修改商品基本信息
    @ApiOperation(value = "修改商品基本信息")
    @PutMapping("/merchant/update")
    public R<String> merchantUpdateProduct(@RequestBody Products products){
        return productsService.merchantUpdateProduct(products);
    }

    //商家查看自己商品的基本信息
    @ApiOperation(value = "商家查看自己商品的基本信息")
    @GetMapping("/merchant/getProductInfo")
    public R<PageDTO<Products>> merchantGetProductInfo(@RequestParam(value = "page",defaultValue = "1") int page,
                                                    @RequestParam(value = "pageSize",defaultValue = "20")int pageSize,
                                                    @RequestParam(value = "productStatus",defaultValue = "APPROVED") ProductStatus productStatus){
        return productsService.merchantGetProductInfo(page,pageSize,productStatus);
    }

    //根据商品id查询基本信息
    @ApiOperation(value = "根据商品id查询基本信息")
    @GetMapping("/getProductBasicInfosAndShopInfosById")
    Map<Long, ProductBasicInfosAndShopInfos> getProductBasicInfosAndShopInfosById(@RequestParam List<Long> productIds){
        return productsService.getProductBasicInfosAndShopInfosById(productIds);
    }

    //根据id查看基本详细信息，以及其配置

    @ApiOperation(value = "根据id查看基本详细信息，以及其配置")
    @GetMapping("/getProductDetailsById/{productId}")
    public R<ProductDetailsVO> getProductDetailsById(@PathVariable Long productId){
        return productsService.getProductDetailsById(productId);
    }

    //管理员审核商品
    @ApiOperation(value = "管理员审核商品")
    @PutMapping("/admin/auditProduct")
    R<String> adminAuditProduct(@RequestParam("productId") Long productId,
                                @RequestParam("productStatus") ProductStatus productStatus){
        return productsService.adminAuditProduct(productId,productStatus);
    }

    //管理员查看待审核商品信息
    @ApiOperation(value = "管理员查看待审核商品信息")
    @GetMapping("/admin/getProductsToAudit")

    R<PageDTO<Products>> adminGetProductsToAudit(@RequestParam(value = "page",defaultValue = "1") int page,
                                              @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                              @RequestParam("productStatus") ProductStatus productStatus){
        return productsService.adminGetProductsToAudit(page,pageSize,productStatus);
    }

    //分页查询所有的商品信息
    //用于es数据的批量插入
    @ApiOperation(value = "分页查询所有的商品信息")
    @GetMapping("/admin/findAllProductsWithPage")
    public PageDTO<ProductDTO> findAllProductsWithPage(@RequestParam(value = "page",defaultValue = "1")int page,
                                                    @RequestParam(value = "pageSize",defaultValue = "1000")int pageSize){
        return productsService.findAllProductsWithPage(page,pageSize);
    }
}
