package com.peacemall.logs.controller;


import com.peacemall.logs.service.StockChangeLogService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("库存变化日志服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stockChangeLog")
public class StockChangeLogeController {
    private final StockChangeLogService stockChangeLogService;

}
