package com.peacemall.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.review.domain.po.Reviews;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewsMapper extends BaseMapper<Reviews> {
}
