package com.peacemall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.product.domain.po.Products;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author watergun
 */
@Mapper
public interface ProductsMapper extends BaseMapper<Products> {

    @Select("SELECT p.product_id, p.brand, p.name, p.description, p.sales, p.updated_at," +
            "c.category_id, c.category_name, " +
            "(SELECT img.url FROM product_images img " +
            "WHERE img.product_id = p.product_id " +
            "ORDER BY img.is_main DESC, img.sort_order ASC LIMIT 1) AS image_url " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "LIMIT #{offset}, #{limit}")
    List<ProductDTO> findProductsWithPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT p.product_id, p.brand, p.name, p.description, p.sales, p.updated_at," +
            "c.category_id, c.category_name, " +
            "(SELECT img.url FROM product_images img " +
            "WHERE img.product_id = p.product_id " +
            "ORDER BY img.is_main DESC, img.sort_order ASC LIMIT 1) AS image_url " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id "+
            "WHERE p.product_id = #{productId}")
    ProductDTO findProductDTOById(@Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM products")
    int countProducts();
}
