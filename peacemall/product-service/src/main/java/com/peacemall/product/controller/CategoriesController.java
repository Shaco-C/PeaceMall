package com.peacemall.product.controller;

import com.peacemall.product.service.CategoriesService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author watergun
 */

@Api("商品分类相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;
}
