package com.peacemall.logs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.logs.domain.po.FlowLog;
import com.peacemall.logs.mapper.FlowLogMapper;
import com.peacemall.logs.service.FlowLogService;
import org.springframework.stereotype.Service;

@Service
public class FlowLogServiceImpl extends ServiceImpl<FlowLogMapper, FlowLog> implements FlowLogService {
}
