package com.peacemall.logs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.logs.domain.po.DeadLetterLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeadLetterLogMapper extends BaseMapper<DeadLetterLog> {
}
