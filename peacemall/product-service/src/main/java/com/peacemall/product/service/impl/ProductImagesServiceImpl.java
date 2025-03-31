package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.dto.ProductImageDTO;
import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.mapper.ProductImagesMapper;
import com.peacemall.product.service.ProductImagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author watergun
 */
@Service
@Slf4j
public class ProductImagesServiceImpl extends ServiceImpl<ProductImagesMapper, ProductImages> implements ProductImagesService {
    @Override
    @Transactional
    public boolean saveProductImages(List<ProductImages> productImages) {
        log.info( "saveProductImages: " + productImages);
        return this.saveBatch(productImages);
    }

    //增加图片
    @Override
    public R<String> addProductImages(ProductImages productImages) {
        log.info("addProductImages: " + productImages);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)){
            log.error("用户权限不足");
            return R.error("用户权限不足");
        }
        log.info("用户权限足够");
        boolean save = this.save(productImages);
        if (!save){
            log.error("保存失败");
            return R.error("保存失败");
        }
        return R.ok("保存成功");
    }

    //删除商品图片
    @Override
    @Transactional
    public R<String> deleteProductImages(List<Long> imageId) {
        log.info("deleteProductImages: " + imageId);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)){
            log.error("用户权限不足");
            return R.error("用户权限不足");
        }
        log.info("用户权限足够");
        boolean remove = this.removeByIds(imageId);
        if (!remove){
            log.error("删除失败");
            return R.error("删除失败");
        }
        return R.ok("删除成功");
    }

    //根据商品id删除所有商品图片
    @Override
    public void deleteProductImagesByProductId(Long productId) {
        log.info("deleteProductImagesByProductId: " + productId);
        LambdaQueryWrapper<ProductImages> productImagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productImagesLambdaQueryWrapper.eq(ProductImages::getProductId, productId);
        this.remove(productImagesLambdaQueryWrapper);
    }

    @Override
    public List<ProductImages> getProductImagesByProductId(Long productId) {
        log.info("getProductImagesByProductId: " + productId);
        LambdaQueryWrapper<ProductImages> productImagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productImagesLambdaQueryWrapper.eq(ProductImages::getProductId, productId);
        return this.list(productImagesLambdaQueryWrapper);
    }

    @Override
    public Map<Long, String> getMainImageUrlsByProductIds(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Collections.emptyMap();
        }

        // 查询每个 productId 的主图（优先 is_main=true，其次 sort_order 最大）
        List<ProductImages> images = baseMapper.selectList(new LambdaQueryWrapper<ProductImages>()
                .in(ProductImages::getProductId, productIds)
                .orderByDesc(ProductImages::getIsMain)  // 先按 is_main = true 排序
                .orderByDesc(ProductImages::getSortOrder)  // 再按 sort_order 降序
        );

        // 用 LinkedHashMap 记录每个 productId 的第一张图
        Map<Long, String> imageMap = new LinkedHashMap<>();
        for (ProductImages image : images) {
            imageMap.putIfAbsent(image.getProductId(), image.getUrl());  // 只存第一张符合条件的图片
        }

        return imageMap;
    }

}
