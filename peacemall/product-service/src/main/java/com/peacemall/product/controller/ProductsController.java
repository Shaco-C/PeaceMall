package com.peacemall.product.controller;

import com.peacemall.product.service.ProductsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author watergun
 */
@Api("商品服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {
    private final ProductsService productsService;
}
