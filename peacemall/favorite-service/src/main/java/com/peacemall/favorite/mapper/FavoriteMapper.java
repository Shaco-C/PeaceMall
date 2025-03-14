package com.peacemall.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.favorite.domain.po.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
