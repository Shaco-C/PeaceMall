package com.peacemall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.product.domain.dto.ProductImageDTO;
import com.peacemall.product.domain.po.ProductImages;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author watergun
 */
@Mapper
public interface ProductImagesMapper extends BaseMapper<ProductImages> {

//    @Select("SELECT img.product_id, img.url " +
//            "FROM product_images img " +
//            "WHERE img.product_id IN (${productIds}) " +
//            "AND img.image_id = ( " +
//            "    SELECT img.image_id FROM product_images sub_img " +
//            "    WHERE sub_img.product_id = img.product_id " +
//            "    ORDER BY sub_img.is_main DESC, sub_img.sort_order ASC LIMIT 1 " +
//            ")")
//    List<ProductImageDTO> getMainImagesByProductIds(@Param("productIds") String productIds);
}
