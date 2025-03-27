package com.peacemall.wallet.controller;


import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.domain.dto.WalletAmountChangeDTO;
import com.peacemall.common.domain.vo.WalletVO;
import com.peacemall.wallet.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author watergun
 */
@RestController
@Api(tags = "钱包服务")
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    //创建用户钱包（用户注册时一起执行）
    @ApiOperation("创建用户钱包（用户注册时一起执行）")
    @PostMapping("/createWalletWhenRegister")
    public void createWalletWhenRegister(@RequestParam("userId") Long userId){

        walletService.createWalletWhenRegister(userId);
    }

    //用户查询自己的钱包信息
    @ApiOperation("用户查询自己的钱包信息")
    @GetMapping("/userGetSelfWalletInfo")
    public R<WalletVO> userGetSelfWalletInfo(){

        return walletService.userGetSelfWalletInfo();
    }

    //管理员通过用户Id查询钱包
    @ApiOperation("管理员通过用户Id查询钱包")
    @GetMapping("/admin/adminGetWalletInfoByUserId")
    public R<WalletVO> adminGetWalletInfoByUserId(@RequestParam("userId")Long userId){

        return walletService.adminGetWalletInfoByUserId(userId);
    }

    //用户充值钱包
    @ApiOperation("用户充值钱包")
    @PutMapping("/userRechargeWallet")
    public R<String> userRechargeWallet(@RequestParam("amount")BigDecimal amount){

        return walletService.userRechargeWallet(amount);
    }

    //用户支付
    //todo 后续要修改
    @ApiOperation("用户支付")
    @PutMapping("/userPay")
    public R<String> userPay(@RequestParam("amount")BigDecimal amount){

        return walletService.userPay(amount);
    }

    //管理员删除用户的钱包
    @ApiOperation("管理员删除用户的钱包")
    @DeleteMapping("/admin/adminDeleteWallet")
    public void adminDeleteWallet(@RequestBody List<Long> userId){

        walletService.adminDeleteWallet(userId);
    }


    //用户钱包待处理金额变化
    @ApiOperation("用户钱包待处理金额变化")
    @PutMapping("/userWalletPendingAmountChange")
    public void userWalletPendingAmountChange(@RequestBody WalletAmountChangeDTO walletAmountChangeDTO){
        walletService.userWalletPendingAmountChange(walletAmountChangeDTO);
    }
}
