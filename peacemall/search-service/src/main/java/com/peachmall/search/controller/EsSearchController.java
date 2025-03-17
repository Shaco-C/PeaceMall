package com.peachmall.search.controller;



import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.query.PageQuery;
import com.peachmall.search.domain.query.ProductPageQuery;
import com.peachmall.search.domain.vo.ProductVO;
import com.peachmall.search.service.EsSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@Api(tags = "搜索操作接口")
@RequiredArgsConstructor
public class EsSearchController {

    private final EsSearchService esSearchService;

    @ApiOperation("用户输入框搜索商品")
    @GetMapping("/userInput")
    R<PageDTO<ProductVO>> searchProduct(@ModelAttribute ProductPageQuery query){
        return esSearchService.searchProduct(query);
    }

    @ApiOperation("根据分类搜索商品")
    @GetMapping("/category")
    R<PageDTO<ProductVO>> searchProductsByCategory(@RequestParam  Long categoryId, @ModelAttribute PageQuery query){
        return esSearchService.searchProductsByCategory(categoryId, query);
    }

    //用户在搜索结果的页面中，点击品牌选项
    //返回该关键字所有的品牌信息
    //当用户点击具体品牌之后，调用searchProduct，然后添加一个brand属性再去搜索
    @ApiOperation("根据搜索关键字获取品牌")
    @GetMapping("/brand")
    R<List<String>> getBrandsBySearchKey(@RequestParam String key){
        return esSearchService.getBrandsBySearchKey(key);
    }
}
