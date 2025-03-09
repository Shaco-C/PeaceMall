package com.peacemall.wallet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.wallet.domain.po.Wallet;
import com.peacemall.common.domain.vo.WalletVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author watergun
 */
public interface WalletService extends IService<Wallet> {

    //创建用户钱包（用户注册时一起执行）
    void createWalletWhenRegister(Long userId);

    //用户查询自己的钱包信息
    R<WalletVO> userGetSelfWalletInfo();

    //管理员通过用户Id查询钱包
    R<WalletVO> adminGetWalletInfoByUserId(Long userId);

    //用户充值钱包
    R<String> userRechargeWallet(BigDecimal amount);

    //用户支付
    R<String> userPay(BigDecimal amount);



    //用户待确认金额变化
    R<String> userPendingBalanceChange(BigDecimal amount);
    //用户钱包余额变化
    R<String> userAvailableBalanceChange(BigDecimal amount);
    //管理员删除用户的钱包
    void adminDeleteWallet(List<Long> userId);

}
