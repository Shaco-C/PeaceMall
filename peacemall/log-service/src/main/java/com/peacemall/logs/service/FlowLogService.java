package com.peacemall.logs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.common.enums.WalletFlowType;
import com.peacemall.logs.domain.po.FlowLog;

public interface FlowLogService extends IService<FlowLog> {

    //根据不同的流水，添加流水日志
    void addFlowLog(FlowLogsDTO flowLogsDTO);
    //查询用户自己的流水日志

    R<Page<FlowLog>> getUserFlowLog(int page, int pageSize, WalletFlowType walletFlowType);


}
