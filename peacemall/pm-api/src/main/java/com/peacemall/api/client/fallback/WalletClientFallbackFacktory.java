package com.peacemall.api.client.fallback;

import com.peacemall.api.client.WalletClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.vo.WalletVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class WalletClientFallbackFacktory implements FallbackFactory<WalletClient> {
    @Override
    public WalletClient create(Throwable cause) {
        return new WalletClient() {
            @Override
            public void createWalletWhenRegister(Long userId) {
                log.error("createWalletWhenRegister error cause", cause);
                log.info("用户创建钱包失败，请重试");
                throw new RuntimeException("用户创建钱包失败，请重试"+cause);
            }

            @Override
            public void adminDeleteWallet(List<Long> userId) {
                log.error("adminDeleteWallet error cause", cause);
                log.info("adminDeleteWallet error cause，请重试");
                throw new RuntimeException("用户创建钱包失败，请重试"+cause);
            }

            @Override
            public R<WalletVO> userGetSelfWalletInfo() {
                log.info("userGetSelfWalletInfo error cause，请重试",cause);
                throw new RuntimeException("用户钱包查询失败，请重试"+cause);
            }
        };
    }
}
