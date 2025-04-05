package com.peacemall.favorite.controller;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.favorite.domain.vo.FavoriteProductVO;
import com.peacemall.favorite.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api("收藏服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    //根据商品的id添加收藏
    @ApiOperation("根据商品的id添加收藏")
    @PostMapping("/add/{productId}")
    public R<String> addFavorite(@PathVariable("productId") Long productId){
        return favoriteService.addFavorite(productId);
    }

    //查看自己的所有收藏
    //需要使用到VO,将商品收藏和商品整合在一起
    @ApiOperation("查看自己的所有收藏")
    @GetMapping("/list")
    public R<PageDTO<FavoriteProductVO>> getUserFavoritesInfo(@RequestParam(value = "page",defaultValue = "1") int page,
                                                              @RequestParam(value = "pageSize",defaultValue = "20")int pageSize){
        return favoriteService.getUserFavoritesInfo(page,pageSize);
    }

    //在收藏的页面中，DTO中包含收藏id
    //根据收藏id删除收藏
    @ApiOperation("根据收藏id删除收藏")
    @DeleteMapping("/delete/{favoriteId}")
    public R<String> deleteFavorite(@PathVariable("favoriteId") Long favoriteId){
        return favoriteService.deleteFavorite(favoriteId);
    }
}
