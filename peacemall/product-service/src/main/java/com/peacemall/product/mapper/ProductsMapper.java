package com.peacemall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.product.domain.po.Products;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author watergun
 */
@Mapper
public interface ProductsMapper extends BaseMapper<Products> {
}
