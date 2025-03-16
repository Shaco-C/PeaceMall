package com.peacemall.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.wallet.domain.po.Wallet;
import com.peacemall.wallet.domain.po.WithdrawRequest;
import com.peacemall.wallet.domain.vo.WithdrawInfoVO;
import com.peacemall.wallet.enums.WithDrawRequestStatus;
import com.peacemall.wallet.mapper.WithdrawRequestMapper;
import com.peacemall.wallet.service.WalletService;
import com.peacemall.wallet.service.WithdrawRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author watergun
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class WithdrawRequestServiceImpl extends ServiceImpl<WithdrawRequestMapper, WithdrawRequest> implements WithdrawRequestService {

    private final WalletService walletService;

    //用户申请提现
    @Override
    @Transactional
    public R<String> userRequestWithdraw(WithdrawInfoVO withdrawInfoVO) {
        log.info("用户申请提现，提现信息为：{}" , withdrawInfoVO);

        //判断提现金额是否合法
        if (withdrawInfoVO.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            log.info("提现金额不合规");
            return R.error("提现金额不合规");
        }

        Long userId = UserContext.getUserId();
        log.info("当前用户id为：{}",userId);

        if (userId==null){
            log.info("用户未登录");
            return R.error("用户未登录");
        }
        //判断用于可用余额是否足够
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId,userId);
        Wallet wallet = walletService.getOne(walletLambdaQueryWrapper);
        if (wallet.getAvailableBalance().compareTo(withdrawInfoVO.getAmount()) < 0){
            log.info("用户余额不足");
            return R.error("用户余额不足");
        }
        //足够的话将可用余额转化为待处理金额
        wallet.setPendingBalance(wallet.getPendingBalance().add(withdrawInfoVO.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(withdrawInfoVO.getAmount()));
        boolean res = walletService.updateById(wallet);
        if (!res){
            log.info("用户余额更新失败");
            return R.error("提交申请失败，请刷新重试");
        }
        //将申请状态设置为待处理
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                userId,
                wallet.getWalletId(),
                withdrawInfoVO.getAmount(),
                WithDrawRequestStatus.PENDING,
                withdrawInfoVO.getPayMethod(),
                withdrawInfoVO.getAccountInfo());
        //提交申请
        boolean save = this.save(withdrawRequest);
        if (!save){
            log.info("提现申请提交失败");
            throw new RuntimeException("提交申请失败");
        }
        log.info("提现申请提交成功");
        return R.ok("申请提交成功");

    }

    //管理员按照状态分页查询提现申请
    @Override
    public R<PageDTO<WithdrawRequest>> adminGetWithdrawRequestByStatus(int page, int pageSize, WithDrawRequestStatus withDrawRequestStatus) {
        log.info("adminGetWithdrawRequestByStatus method is called");
        if (page < 1 || pageSize < 1) {
            return R.error("分页参数无效");
        }

        Long userId = UserContext.getUserId();
        String userRole =UserContext.getUserRole();
        //验证用户权限是否足够，是否为管理员
        //查看用户是否登陆
        if(userId == null || !UserRole.ADMIN.name().equals(userRole)){
            log.info("用户未登录或权限不足");
            return R.error("用户未登录或权限不足");
        }

        //构造查询条件
        LambdaQueryWrapper<WithdrawRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawRequest::getStatus,withDrawRequestStatus);

        //分页查询
        Page<WithdrawRequest> withdrawRequestPage = new Page<>(page, pageSize);
        this.page(withdrawRequestPage, queryWrapper);

        return R.ok(PageDTO.of(withdrawRequestPage));
    }

    //管理员审核提现申请
    @Override
    @Transactional
    public R<String> adminCheckWithdrawRequest(Long id, WithDrawRequestStatus withDrawRequestStatus) {
        log.info("adminCheckWithdrawRequest method is called");
        Long userId = UserContext.getUserId();
        String userRole =UserContext.getUserRole();
        //验证用户权限是否足够，是否为管理员
        //查看用户是否登陆
        if(userId == null || !UserRole.ADMIN.name().equals(userRole)){
            log.info("用户未登录或权限不足");
            return R.error("用户未登录或权限不足");
        }
        //查看申请是否存在
        WithdrawRequest withdrawRequest = this.getById(id);
        if (withdrawRequest == null){
            log.info("申请不存在");
            return R.error("申请不存在");
        }
        //查看申请状态是否为待处理
        if (!WithDrawRequestStatus.PENDING.equals(withdrawRequest.getStatus())){
            log.info("申请状态不为待处理");
            return R.error("该申请已处理，请刷新重试");
        }
        //修改申请状态
        withdrawRequest.setStatus(withDrawRequestStatus);
        boolean res = this.updateById(withdrawRequest);
        if (!res){
            log.info("申请状态修改失败");
            return R.error("申请状态修改失败，请刷新重试");
        }
        //更根据不同状态对钱包进行不同处理
        Wallet wallet = walletService.getById(withdrawRequest.getWalletId());

        //不管是同意提现还是拒绝提现，待处理金额都会减少
        wallet.setPendingBalance(wallet.getPendingBalance().subtract(withdrawRequest.getAmount()));
        //如果同意提现的话，无需改变
        //如果拒绝提现的话,需要把待处理金额返还给可用金额
        if (WithDrawRequestStatus.REJECTED.equals(withdrawRequest.getStatus())){
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(withdrawRequest.getAmount()));
        }
        //更新钱包总余额
        wallet.setTotalBalance(wallet.getPendingBalance().add(wallet.getAvailableBalance()));
        boolean update = walletService.updateById(wallet);
        if (!update){
            log.info("钱包更新失败");
            throw new RuntimeException("钱包更新失败");
        }
        log.info("申请状态修改成功");

        //todo 添加流水日志

        return R.ok("申请状态修改成功");
    }

    //用户查询自己的提现申请
    @Override
    public R<PageDTO<WithdrawRequest>> userGetWithdrawRequest(int page, int pageSize) {
        log.info("userGetWithdrawRequest method is called");
        if (page < 1 || pageSize < 1) {
            return R.error("分页参数无效");
        }

        Long userId = UserContext.getUserId();

        if (userId == null){
            log.info("用户未登录");
            return R.error("用户未登录");
        }
        //构造查询条件
        LambdaQueryWrapper<WithdrawRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawRequest::getUserId,userId);

        //分页查询
        Page<WithdrawRequest> withdrawRequestPage = new Page<>(page, pageSize);
        this.page(withdrawRequestPage, queryWrapper);
        return R.ok(PageDTO.of(withdrawRequestPage));
    }

    //用户取消提现申请
    @Override
    @Transactional
    public R<String> userCancelWithdrawRequest(Long withdrawRequestId) {
        log.info("userCancelWithdrawRequest method is called");
        Long userId = UserContext.getUserId();
        if (userId == null){
            log.info("用户未登录");
            return R.error("用户未登录");
        }
        WithdrawRequest withdrawRequest = this.getById(withdrawRequestId);
        if (withdrawRequest == null){
            log.info("申请不存在");
            return R.error("申请不存在");
        }
        //判断该申请是否是该用户的
        if (!userId.equals(withdrawRequest.getUserId())){
            log.info("该申请不属于该用户");
            return R.error("该申请不属于该用户");
        }
        //判断该申请是否处于待处理状态
        if (!WithDrawRequestStatus.PENDING.equals(withdrawRequest.getStatus())){
            log.info("该申请已处理");
            return R.error("该申请已处理，请刷新重试");
        }
        //修改申请状态
        withdrawRequest.setStatus(WithDrawRequestStatus.CANCELED);
        boolean res = this.updateById(withdrawRequest);
        if (!res){
            log.info("申请状态修改失败");
            return R.error("申请状态修改失败，请刷新重试");
        }
        log.info("申请状态修改成功");

        //待处理金额需要返还给可用金额
        Wallet wallet = walletService.getById(withdrawRequest.getWalletId());
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(withdrawRequest.getAmount()));
        wallet.setPendingBalance(wallet.getPendingBalance().subtract(withdrawRequest.getAmount()));
        wallet.setTotalBalance(wallet.getAvailableBalance().add(wallet.getPendingBalance()));
        boolean update = walletService.updateById(wallet);
        if (!update){
            log.info("钱包更新失败");
            throw new RuntimeException("钱包更新失败");
        }
        log.info("申请状态修改成功");
        return R.ok("申请状态修改成功");
    }
}
