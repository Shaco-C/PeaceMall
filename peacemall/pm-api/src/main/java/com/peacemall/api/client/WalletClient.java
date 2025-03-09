package com.peacemall.api.client;


import com.peacemall.api.client.fallback.WalletClientFallbackFacktory;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.vo.WalletVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "wallet-service",fallbackFactory = WalletClientFallbackFacktory.class)

public interface WalletClient {

    //创建用户钱包（用户注册时一起执行）
    @PostMapping("/wallet/createWalletWhenRegister")
    void createWalletWhenRegister(@RequestParam("userId") Long userId);
    @DeleteMapping("/wallet/admin/adminDeleteWallet")
    void adminDeleteWallet(@RequestParam("ids") List<Long> userId);

    @GetMapping("/wallet/userGetSelfWalletInfo")
    R<WalletVO> userGetSelfWalletInfo();
}
