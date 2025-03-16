package com.peacemall.logs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.peacemall.common.domain.R;
import com.peacemall.common.enums.WalletFlowType;
import com.peacemall.logs.domain.po.FlowLog;
import com.peacemall.logs.service.FlowLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("流水日志服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowLog")
public class FlowLogController {
    private final FlowLogService flowLogService;

    //获取用户流水日志
    @ApiOperation(value = "获取用户流水日志")
    @GetMapping("/getUserFlowLog")
    R<PageDTO<FlowLog>> getUserFlowLog(@RequestParam(value = "page",defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                       @RequestParam(value = "walletFlowType",required = false) WalletFlowType walletFlowType) {
        return flowLogService.getUserFlowLog(page, pageSize, walletFlowType);
    }

}
