package com.peacemall.logs.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.logs.domain.po.DeadLetterLog;
import com.peacemall.logs.enums.DeadLetterLogStatus;
import com.peacemall.logs.service.DeadLetterLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api("死信日志服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dead-letter-log")
public class DeadLetterLogController {

    private final DeadLetterLogService deadLetterLogService;

    //获取特定status死信日志
    @ApiOperation("获取特定status死信日志")
    @GetMapping("/admin/getDeadLetterLogByStatus")
    public R<PageDTO<DeadLetterLog>> getDeadLetterLogByStatus(@RequestParam(value = "page",defaultValue = "1") int page,
                                                       @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                                       @RequestParam(value = "status",defaultValue = "PENDING") DeadLetterLogStatus status){
        return deadLetterLogService.getDeadLetterLogByStatus(page, pageSize, status);
    }
    //更新死信日志状态
    @ApiOperation("更新死信日志状态")
    @PutMapping("/admin/updateDeadLetterLogStatus")
    public R<String> updateDeadLetterLogStatus(@RequestParam("deadLetterLogId") Long deadLetterLogId,
                                               @RequestParam("status") DeadLetterLogStatus status){
        return deadLetterLogService.updateDeadLetterLogStatus(deadLetterLogId, status);
    }

}
