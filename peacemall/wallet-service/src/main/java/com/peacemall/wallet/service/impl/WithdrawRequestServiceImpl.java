package com.peacemall.wallet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.wallet.domain.po.WithdrawRequest;
import com.peacemall.wallet.enums.WithDrawRequestStatus;
import com.peacemall.wallet.mapper.WithdrawRequestMapper;
import com.peacemall.wallet.service.WithdrawRequestService;

import java.math.BigDecimal;

/**
 * @author watergun
 */
public class WithdrawRequestServiceImpl extends ServiceImpl<WithdrawRequestMapper, WithdrawRequest> implements WithdrawRequestService {
    @Override
    public R<String> userRequestWithdraw(BigDecimal amount) {
        return null;
    }

    @Override
    public R<Page<WithdrawRequest>> adminGetWithdrawRequestByStatus(int page, int pageSize, WithDrawRequestStatus withDrawRequestStatus) {
        return null;
    }

    @Override
    public R<String> adminCheckWithdrawRequest(Long id, WithDrawRequestStatus withDrawRequestStatus) {
        return null;
    }

    @Override
    public R<Page<WithdrawRequest>> userGetWithdrawRequest(int page, int pageSize) {
        return null;
    }

    @Override
    public R<String> userCancelWithdrawRequest(Long withdrawRequestId) {
        return null;
    }
}
