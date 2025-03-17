package com.peacemall.logs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.enums.WalletFlowType;
import com.peacemall.common.utils.UserContext;
import com.peacemall.logs.domain.po.FlowLog;
import com.peacemall.logs.mapper.FlowLogMapper;
import com.peacemall.logs.service.FlowLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FlowLogServiceImpl extends ServiceImpl<FlowLogMapper, FlowLog> implements FlowLogService {
    @Override
    public void addFlowLog(FlowLogsDTO flowLogsDTO) {
        log.info("addFlowLog method is called,flowLogsDTO{}",flowLogsDTO);
        FlowLog flowLog = new FlowLog();
        BeanUtil.copyProperties(flowLogsDTO, flowLog);
        log.info("flowLog:{}", flowLog);
        boolean save = this.save(flowLog);
        if (!save) {
            log.error("添加流水日志失败");
            throw new RuntimeException("添加流水日志失败");
        }
    }




    @Override
    public R<PageDTO<FlowLog>> getUserFlowLog(int page, int pageSize, WalletFlowType walletFlowType) {
        log.info("getUserFlowLog method is called,page:{},pageSize:{},walletFlowType:{}", page, pageSize, walletFlowType);
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登陆");
            return R.error("用户未登陆");
        }
        LambdaQueryWrapper<FlowLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowLog::getUserId, userId);
        if (walletFlowType != null) {
            queryWrapper.eq(FlowLog::getFlowType, walletFlowType);
        }
        queryWrapper.orderByDesc(FlowLog::getCreatedAt);
        Page<FlowLog> flowLogPage = new Page<>(page, pageSize);
        this.page(flowLogPage, queryWrapper);
        return R.ok(PageDTO.of(flowLogPage));
    }
}
