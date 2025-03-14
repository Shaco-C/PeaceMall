package com.peacemall.logs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.logs.domain.po.StockChangeLog;
import com.peacemall.logs.mapper.StockChangeLogMapper;
import com.peacemall.logs.service.StockChangeLogService;
import org.springframework.stereotype.Service;

@Service
public class StockChangeLogServiceImpl extends ServiceImpl<StockChangeLogMapper, StockChangeLog> implements StockChangeLogService {
}
