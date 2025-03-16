package com.peacemall.favorite.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.favorite.domain.po.Favorite;
import com.peacemall.favorite.domain.vo.FavoriteProductVO;

public interface FavoriteService extends IService<Favorite> {

    //根据商品的id添加收藏
    R<String> addFavorite(Long productId);

    //查看自己的所有收藏
    //需要使用到VO,将商品收藏和商品整合在一起
    R<PageDTO<FavoriteProductVO>> getUserFavoritesInfo(int page, int pageSize);

    //在收藏的页面中，DTO中包含收藏id
    //根据收藏id删除收藏
    R<String> deleteFavorite(Long favoriteId);

}
