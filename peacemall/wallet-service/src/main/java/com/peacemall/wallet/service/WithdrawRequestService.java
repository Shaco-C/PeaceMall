package com.peacemall.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.wallet.domain.po.WithdrawRequest;
import com.peacemall.wallet.domain.vo.WithdrawInfoVO;
import com.peacemall.wallet.enums.WithDrawRequestStatus;

import java.math.BigDecimal;

/**
 * @author watergun
 */
public interface WithdrawRequestService extends IService<WithdrawRequest> {

    //用户申请提现
    R<String> userRequestWithdraw(WithdrawInfoVO withdrawInfoVO);

    //管理员按照状态分页查询提现申请
    R<Page<WithdrawRequest>> adminGetWithdrawRequestByStatus(int page, int pageSize, WithDrawRequestStatus withDrawRequestStatus);

    //管理员审核提现申请
    R<String> adminCheckWithdrawRequest(Long id, WithDrawRequestStatus withDrawRequestStatus);

    //用户查询自己的提现申请
    R<Page<WithdrawRequest>> userGetWithdrawRequest(int page, int pageSize);

    //用户取消提现申请
    R<String> userCancelWithdrawRequest(Long withdrawRequestId);
}
