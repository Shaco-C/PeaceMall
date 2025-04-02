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


    // 搜索页面中还要有一个品牌的bar可以选择，
    // 用户点击品牌的bar之后，调用getBrandsBySearchKey方法，获得该key的所有品牌
    // 然后用户点击某个品牌，调用searchProduct，然后添加一个brand属性再去搜索
    @ApiOperation("用户输入框搜索商品")
    @GetMapping("/userInput")
    public R<PageDTO<ProductVO>> searchProduct(@ModelAttribute ProductPageQuery query){
        return esSearchService.searchProduct(query);
    }

    //首页下滑瀑布商品展示
    @ApiOperation("瀑布流商品展示")
    @GetMapping("/search_after")
    public R<List<ProductVO>> getProductListBySearchAfter(
            @RequestParam(value = "lastProductId", required = false) Long lastProductId,
            @RequestParam(value = "size", defaultValue = "18") int size){
        return esSearchService.getProductListBySearchAfter(lastProductId, size);
    }

    //获取首页热销商品
    @ApiOperation("获取热销商品")
    @GetMapping("/hotSales")
    public R<List<ProductVO>> getHotSalesProducts(@RequestParam(value = "topN",defaultValue = "18") int topN){
        return esSearchService.getHotSalesProducts(topN);
    }

    @ApiOperation("根据分类搜索商品")
    @GetMapping("/category")
    public R<PageDTO<ProductVO>> searchProductsByCategory(@RequestParam  Long categoryId, @ModelAttribute PageQuery query){
        return esSearchService.searchProductsByCategory(categoryId, query);
    }

    //用户在搜索结果的页面中，点击品牌选项
    //返回该关键字所有的品牌信息
    //当用户点击具体品牌之后，调用searchProduct，然后添加一个brand属性再去搜索
    @ApiOperation("根据搜索关键字获取品牌")
    @GetMapping("/brand")
    public R<List<String>> getBrandsBySearchKey(@RequestParam String key){
        return esSearchService.getBrandsBySearchKey(key);
    }
}
