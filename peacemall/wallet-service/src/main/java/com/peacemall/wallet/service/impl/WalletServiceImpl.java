package com.peacemall.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.wallet.domain.po.Wallet;
import com.peacemall.wallet.domain.vo.WalletVO;
import com.peacemall.wallet.mapper.WalletMapper;
import com.peacemall.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author watergun
 */
@Service
@Slf4j
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {


    //创建用户钱包（用户注册时一起执行）
    @Override
    public R<String> createWalletWhenRegister(Long userId) {
        log.info("createWalletWhenRegister method is called");
        log.info("userId:{}", userId);
        if (userId == null) {
            log.error("用户参数为空");
            return R.error("用户参数为空");
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setAvailableBalance(BigDecimal.valueOf(0));
        wallet.setTotalBalance(BigDecimal.valueOf(0));
        wallet.setPendingBalance(BigDecimal.valueOf(0));

        boolean save = this.save(wallet);
        if (!save) {
            log.error("创建钱包失败");
            return R.error("创建钱包失败");
        }
        return R.ok("创建钱包成功");
    }

    //用户查询自己的钱包信息
    @Override
    public R<WalletVO> userGetSelfWalletInfo() {
        log.info("serGetSelfWalletInfo method is called");
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        log.info("userId:{}", userId);

        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);

        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }

        WalletVO walletVO = new WalletVO();
        log.info("wallet:{}", walletVO);
        BeanUtil.copyProperties(wallet, walletVO);

        return R.ok(walletVO);
    }


    //管理员通过用户Id查询钱包
    @Override
    public R<WalletVO> adminGetWalletInfoByUserId(Long userId) {
        log.info("adminGetWalletInfoByUserId method is called");
        log.info("userId:{}", userId);
        Long userIdFromToken = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userIdFromToken == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户权限不足");
        }
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        WalletVO walletVO = new WalletVO();
        BeanUtil.copyProperties(wallet, walletVO);
        log.info("user:{} wallet info is:{}", userId, walletVO);
        return R.ok(walletVO);
    }

    //用户充值钱包
    @Override
    public R<String> userRechargeWallet(BigDecimal amount) {
        log.info("userRechargeWallet method is called");
        if ((amount == null) || (amount.compareTo(BigDecimal.valueOf(0))<=0)) {
            log.error("用户充值金额不合法");
            return R.error("充值金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("userId:{}", userId);
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet.setTotalBalance(wallet.getTotalBalance().add(amount));
        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户充值失败");
            return R.error("用户充值失败");
        }
        //todo 添加流水日志
        return R.ok("用户充值成功");
    }

    //用户支付
    @Override
    @Transactional
    public R<String> userPay(BigDecimal amount) {
        log.info("userPay method is called");
        if ((amount == null) || (amount.compareTo(BigDecimal.valueOf(0))<=0)) {
            log.error("用户支付金额不合法");
            return R.error("支付金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("userId:{}", userId);
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            log.error("用户余额不足");
            return R.error("用户余额不足");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalBalance(wallet.getTotalBalance().subtract(amount));
        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户支付失败");
            return R.error("用户支付失败");
        }
        //todo 添加流水日志
        return R.ok("用户支付成功");
    }

    //用户待确认金额变化
    @Override
    @Transactional
    public R<String> userPendingBalanceChange(BigDecimal amount) {
        log.info("userPendingBalanceChange is called:amount:{}", amount);
        if (amount == null){
            log.error("用户待处理金额修改金额不合法");
            return R.error("用户待处理金额修改金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }

        //查看是增加待处理金额还是减少待处理金额
        //如果是减少待处理金额 ，查看待处理金额余额是否足够 ，如果不够则报错
        if ((amount.compareTo(BigDecimal.valueOf(0))<=0)&&(amount.abs().compareTo(wallet.getPendingBalance())>0)){
            log.error("用户待处理金额不足");
            return R.error("用户待处理金额不足");
        }

        wallet.setPendingBalance(wallet.getPendingBalance().add(amount));
        wallet.setTotalBalance(wallet.getAvailableBalance().add(wallet.getPendingBalance()));

        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户待处理金额修改失败");
            return R.error("用户待处理金额修改失败");
        }
        return R.ok("用户待处理金额修改成功");
    }

    //用户钱包余额变化
    @Override
    public R<String> userAvailableBalanceChange(BigDecimal amount) {
        log.info("userBalanceChange method is called");
        if (amount == null){
            log.error("用户余额修改金额不合法");
            return R.error("用户余额修改金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");

        }
        if ((amount.compareTo(BigDecimal.valueOf(0))<=0)&&(amount.abs().compareTo(wallet.getPendingBalance())>0)) {
            log.error("用可用金额不足，参数错误");
            return R.error("用可用金额不足，参数错误");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet.setTotalBalance(wallet.getAvailableBalance().add(wallet.getPendingBalance()));
        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户支付失败");
            return R.error("用户支付失败");
        }
        return R.ok("用户支付成功");
    }

    //管理员删除用户的钱包
    @Override
    public R<String> adminDeleteWallet(Long userId) {
        log.info("adminDeleteWallet method is called");
        if (userId == null) {
            log.error("用户id不合法");
            return R.error("用户id不合法");
        }
        Long userIdFromToken = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userIdFromToken:{},userRole:{}",userIdFromToken,userRole);
        if (userIdFromToken == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户不是管理员");
        }

        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        boolean remove = this.removeById(wallet.getWalletId());
        if (!remove) {
            log.error("用户钱包删除失败");
        }
        return R.ok("用户钱包删除成功");
    }
}
