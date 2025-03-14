package com.peacemall.logs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peacemall.logs.domain.po.FlowLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowLogMapper extends BaseMapper<FlowLog> {
}
