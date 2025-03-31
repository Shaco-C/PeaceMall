package com.peacemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.product.domain.po.ProductImages;

import java.util.List;
import java.util.Map;

/**
 * @author watergun
 */
public interface ProductImagesService extends IService<ProductImages> {

    //保存商品图片
    boolean saveProductImages(List<ProductImages> productImages);

    //增加图片
    R<String> addProductImages(ProductImages productImages);

    //删除商品图片
    R<String> deleteProductImages(List<Long> imageId);

    //根据商品id删除所有商品图片
    void deleteProductImagesByProductId(Long productId);

    //根据商品id查询所有商品图片
    List<ProductImages> getProductImagesByProductId(Long productId);

    Map<Long, String> getMainImageUrlsByProductIds(List<Long> productIds);
}
