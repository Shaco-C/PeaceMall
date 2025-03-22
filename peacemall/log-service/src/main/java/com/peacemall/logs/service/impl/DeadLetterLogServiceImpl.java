package com.peacemall.logs.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.logs.domain.po.DeadLetterLog;
import com.peacemall.logs.enums.DeadLetterLogStatus;
import com.peacemall.logs.mapper.DeadLetterLogMapper;
import com.peacemall.logs.service.DeadLetterLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeadLetterLogServiceImpl extends ServiceImpl<DeadLetterLogMapper, DeadLetterLog> implements DeadLetterLogService {

    @Override
    public R<PageDTO<DeadLetterLog>> getDeadLetterLogByStatus(int page, int pageSize, DeadLetterLogStatus status) {
        log.info("getDeadLetterLogByStatus:{}", status);
        log.info("page:{},pageSize:{}", page, pageSize);
        if (page<1 || pageSize<1){
            log.info("分页参数错误");
            return R.error("分页参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userId:{},userRole:{}", userId, userRole);
        if (userId== null || !UserRole.ADMIN.name().equals(userRole)){
            log.info("没有权限");
            return R.error("没有权限");
        }
        log.info("查询死信日志");
        LambdaQueryWrapper<DeadLetterLog> deadLetterLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deadLetterLogLambdaQueryWrapper.eq(DeadLetterLog::getStatus, status)
                .orderByDesc(DeadLetterLog::getCreatedAt);
        Page<DeadLetterLog> deadLetterLogPage = new Page<>(page,pageSize);
        this.page(deadLetterLogPage, deadLetterLogLambdaQueryWrapper);
        return R.ok(PageDTO.of(deadLetterLogPage));
    }

    @Override
    public R<String> updateDeadLetterLogStatus(Long deadLetterLogId, DeadLetterLogStatus status) {
        log.info("updateDeadLetterLogStatus:{}", deadLetterLogId);
        log.info("status:{}", status);
        if (deadLetterLogId == null || status == null){
            log.info("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userId:{},userRole:{}", userId, userRole);
        if (userId== null || !UserRole.ADMIN.name().equals(userRole)){
            log.info("没有权限");
            return R.error("没有权限");
        }
        if (!DeadLetterLogStatus.PENDING.equals(status)){
            log.info("已经被处理过了");
            return R.error("已经被处理过了");
        }
        log.info("更新死信日志状态");
        DeadLetterLog deadLetterLog = this.getById(deadLetterLogId);
        if (deadLetterLog == null){
            log.info("死信日志不存在");
            return R.error("死信日志不存在");
        }
        deadLetterLog.setStatus(status);
        boolean updated = this.updateById(deadLetterLog);
        if (!updated){
            log.info("更新失败");
            return R.error("更新失败");
        }
        return R.ok("更新成功");
    }

    @Override
    public void saveToDeadLetterDatabase(FlowLogsDTO flowLogsDTO, String reason) {
        log.info("saveToDeadLetterDatabase:{}", flowLogsDTO);
        log.info("reason:{}", reason);

        DeadLetterLog deadLetterLog = new DeadLetterLog();
        deadLetterLog.setReason(reason);
        deadLetterLog.setMessage(JSONUtil.toJsonStr(flowLogsDTO));
        deadLetterLog.setStatus(DeadLetterLogStatus.PENDING);
        boolean saved = this.save(deadLetterLog);
        log.info("死信日志保存结果: {}", saved);
        if (!saved) {
            log.error("保存死信日志失败，详情: {}", deadLetterLog);
            throw new RuntimeException("保存死信日志失败");
        }
    }

    @Override
    public void saveToDeadLetterDatabase(String message, String reason) {
        log.info("saveToDeadLetterDatabase:message:{},reason:{}", message, reason);
        DeadLetterLog deadLetterLog = new DeadLetterLog();
        deadLetterLog.setReason(reason);
        deadLetterLog.setMessage(message);
        deadLetterLog.setStatus(DeadLetterLogStatus.PENDING);
        boolean saved = this.save(deadLetterLog);
        log.info("死信日志保存结果: {}", saved);
        if (!saved) {
            log.error("保存死信日志失败，详情: {}", deadLetterLog);
            throw new RuntimeException("保存死信日志失败");
        }
    }
}
