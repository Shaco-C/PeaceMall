package com.peacemall.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import com.peacemall.common.utils.UserContext;
import com.peacemall.favorite.domain.po.Favorite;
import com.peacemall.favorite.domain.vo.FavoriteProductVO;
import com.peacemall.favorite.mapper.FavoriteMapper;
import com.peacemall.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final ProductClient productClient;
    @Override
    public R<String> addFavorite(Long productId) {
        log.info("addFavorite method is called");
        if (productId == null){
            log.info("商品信息为空");
            return R.error("商品id不能为空");
        }

        Long userId = UserContext.getUserId();
        if (userId == null){
            log.info("用户未登录");
            return R.error("用户未登录");
        }

        //查看用户是否收藏过该商品
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        Favorite favoriteExist = this.getOne(favoriteLambdaQueryWrapper);
        if (favoriteExist != null){
            log.info("用户已收藏过该商品");
            return R.error("用户已收藏过该商品");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        boolean save = this.save(favorite);
        if (!save){
            log.info("收藏失败");
            return R.error("收藏失败");
        }
        return R.ok("收藏成功");
    }

    @Override
    public R<PageDTO<FavoriteProductVO>> getUserFavoritesInfo(int page, int pageSize) {
        log.info("getUserFavoritesInfo method is called");

        if (page < 1 || pageSize < 1) {
            log.warn("分页参数错误");
            return R.error("分页参数错误");
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.warn("用户未登录");
            return R.error("用户未登录");
        }

        // 分页查询用户收藏的商品 ID
        Page<Favorite> favoritePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoriteLambdaQueryWrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);
        this.page(favoritePage, favoriteLambdaQueryWrapper);
        log.info("分页成功");
        List<Favorite> favorites = favoritePage.getRecords();
        if (favorites.isEmpty()) {
            return R.ok(PageDTO.of(new Page<>(page, pageSize, 0)) ); // 直接返回空分页
        }
        log.info("获取收藏商品信息成功");

        // 获取所有 productId
        List<Long> productIds = favorites.stream().map(Favorite::getProductId).collect(Collectors.toList());

        // 调用 Feign 获取商品信息
        Map<Long, ProductBasicInfosAndShopInfos> productBasicInfos = productClient.getProductBasicInfosAndShopInfosById(productIds);

        // 组装收藏商品信息
        List<FavoriteProductVO> favoriteProductVOS = favorites.stream()
                .map(favorite -> new FavoriteProductVO(favorite.getFavoritesId(), productBasicInfos.get(favorite.getProductId())))
                .collect(Collectors.toList());

        // 创建分页结果
        Page<FavoriteProductVO> favoriteProductVOPage = new Page<>(page, pageSize, favoritePage.getTotal());
        favoriteProductVOPage.setRecords(favoriteProductVOS);

        return R.ok(PageDTO.of(favoriteProductVOPage));
    }


    @Override
    public R<String> deleteFavorite(Long favoriteId) {
        log.info("deleteFavorite method is called");
        if (favoriteId == null){
            log.info("收藏id为空");
            return R.error("收藏id不能为空");
        }
        Long userId = UserContext.getUserId();
        if (userId == null){
            log.info("用户未登录");
            return R.error("用户未登录");
        }
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null ){
            log.info("收藏不存在");
            return R.error("收藏不存在");
        }
        if (!Objects.equals(favorite.getUserId(), userId)){
            log.info("用户id不匹配");
            return R.error("权限不足");
        }
        boolean remove = this.removeById(favoriteId);
        if (!remove){
            log.info("删除失败");
            return R.error("删除失败");
        }
        return R.ok("删除成功");
    }
}
