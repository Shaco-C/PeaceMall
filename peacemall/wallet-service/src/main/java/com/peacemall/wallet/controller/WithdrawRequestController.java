package com.peacemall.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.wallet.domain.po.WithdrawRequest;
import com.peacemall.wallet.domain.vo.WithdrawInfoVO;
import com.peacemall.wallet.enums.WithDrawRequestStatus;
import com.peacemall.wallet.service.WithdrawRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author watergun
 */

@Api(tags = "提现请求服务")
@RestController
@RequestMapping("/withdraw")
@RequiredArgsConstructor
public class WithdrawRequestController {
    private final WithdrawRequestService withdrawRequestService;

    //用户申请提现
    @ApiOperation(value = "用户申请提现")
    @PostMapping("/userRequestWithdraw")
    public R<String> userRequestWithdraw(@RequestBody WithdrawInfoVO withdrawInfoVO){

        return withdrawRequestService.userRequestWithdraw(withdrawInfoVO);
    }

    //管理员按照状态分页查询提现申请
    @ApiOperation(value = "管理员按照状态分页查询提现申请")
    @GetMapping("/admin/adminGetWithdrawRequestByStatus")
    public R<PageDTO<WithdrawRequest>> adminGetWithdrawRequestByStatus(@RequestParam(value = "page",defaultValue = "1") int page,
                                                                       @RequestParam(value = "pageSize",defaultValue = "20")int pageSize,
                                                                       @RequestParam(value = "withDrawRequestStatus",defaultValue = "PENDING")WithDrawRequestStatus withDrawRequestStatus){

        return withdrawRequestService.adminGetWithdrawRequestByStatus(page,pageSize,withDrawRequestStatus);
    }

    //管理员审核提现申请
    @ApiOperation(value = "管理员审核提现申请")
    @PutMapping("/admin/adminCheckWithdrawRequest")
    public R<String> adminCheckWithdrawRequest(@RequestParam(value = "id") Long id,
                                               @RequestParam(value = "withDrawRequestStatus") WithDrawRequestStatus withDrawRequestStatus){

        return withdrawRequestService.adminCheckWithdrawRequest(id,withDrawRequestStatus);
    }

    //用户查询自己的提现申请
    @ApiOperation(value = "用户查询自己的提现申请")
    @GetMapping("/userGetWithdrawRequest")
    public R<PageDTO<WithdrawRequest>> userGetWithdrawRequest(@RequestParam(value = "page",defaultValue = "1") int page,
                                                           @RequestParam(value = "pageSize",defaultValue = "20")int pageSize,
                                                              @RequestParam(value = "withDrawRequestStatus",defaultValue = "PENDING")WithDrawRequestStatus withDrawRequestStatus){

        return withdrawRequestService.userGetWithdrawRequest(page,pageSize,withDrawRequestStatus);
    }

    //用户取消提现申请
    @ApiOperation(value = "用户取消提现申请")
    @PutMapping("/userCancelWithdrawRequest")
    public R<String> userCancelWithdrawRequest(@RequestParam(value = "withdrawRequestId") Long withdrawRequestId){

        return withdrawRequestService.userCancelWithdrawRequest(withdrawRequestId);
    }

}
