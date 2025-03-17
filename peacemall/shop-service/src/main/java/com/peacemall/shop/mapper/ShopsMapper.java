package com.peacemall.shop.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.common.domain.dto.ShopDTO;

import com.peacemall.shop.domain.po.Shops;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author watergun
 */
@Mapper
public interface ShopsMapper extends BaseMapper<Shops> {
    @Select("SELECT shop_id, user_id, shop_name, shop_description, updated_at, shop_avatar_url " +
            "FROM shops " +
            "LIMIT #{offset}, #{limit}")
    List<ShopDTO> findShopsWithPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM shops")
    int countShops();
}
