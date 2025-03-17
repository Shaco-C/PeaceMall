package com.peacemall.logs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.logs.domain.po.DeadLetterLog;
import com.peacemall.logs.enums.DeadLetterLogStatus;

public interface DeadLetterLogService extends IService<DeadLetterLog> {

    //获取特定status死信日志
    R<PageDTO<DeadLetterLog>> getDeadLetterLogByStatus(int page,int pageSize,DeadLetterLogStatus status);
    //更新死信日志状态
    R<String> updateDeadLetterLogStatus(Long deadLetterLogId, DeadLetterLogStatus status);
    //添加死信日志
    void saveToDeadLetterDatabase(FlowLogsDTO flowLogsDTO,String reason);
}
