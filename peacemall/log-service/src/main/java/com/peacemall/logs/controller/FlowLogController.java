package com.peacemall.logs.controller;

import com.peacemall.logs.service.FlowLogService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("流水日志服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowLog")
public class FlowLogController {
    private final FlowLogService flowLogService;

}
