package com.peacemall.product.controller;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.product.domain.po.Categories;
import com.peacemall.product.service.CategoriesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author watergun
 */

@Api("商品分类相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    //用户查看分类
    //类别按parentId分组
    @ApiOperation(value = "用户查看分类")
    @GetMapping("/getCategories")
    R<Map<String, List<Categories>>> getCategoriesByParentId(){
        return categoriesService.getCategoriesByParentId();
    }

    //用户查看分类
    //类别按parentId分组
    @GetMapping("/getCategoriesInfoByParentId")
    R<List<Categories>> getCategoriesInfoByParentId(@RequestParam("parentId") Long parentId){
        return categoriesService.getCategoriesInfoByParentId(parentId);
    }

    //管理员创建分类
    @ApiOperation(value = "管理员创建分类")
    @PostMapping("/admin/createCategories")
    R<String> createCategories(@RequestBody Categories categories){
        return categoriesService.createCategories(categories);

    }


    //管理员删除分类
    @ApiOperation("管理员删除分类")
    @DeleteMapping("/admin/deleteCategories/{categoryId}")
    R<String> deleteCategories(@PathVariable("categoryId") Long categoryId){
        return categoriesService.deleteCategories(categoryId);

    }
    //管理员修改分类
    @ApiOperation("value = 管理员修改分类")
    @PutMapping("/admin/updateCategories")
    R<String> updateCategories(@RequestBody Categories categories){
        return categoriesService.updateCategories(categories);

    }

    //管理员分页查看所有分类
    @ApiOperation("管理员分页查看所有分类")
    @GetMapping("/admin/getCategoriesByPage")
    R<PageDTO<Categories>> getCategoriesByPage(@RequestParam(value = "page",defaultValue = "1") int page,
                                               @RequestParam(value = "pageSize",defaultValue = "20") int pageSize){
        return categoriesService.getCategoriesByPage(page,pageSize);

    }

    //返回一个分类的所有子分类，包括自己
    @ApiOperation("返回一个分类的所有子分类，包括自己")
    @GetMapping("/getSubCategoryIds")
    List<Long> getSubCategoryIds(@RequestParam("categoryId") Long categoryId){
        return categoriesService.getSubCategoryIds(categoryId);
    }
}
