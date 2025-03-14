package com.peacemall.api.client.fallback;

import com.peacemall.api.client.ShopClient;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;
import java.util.Map;

@Slf4j
public class ShopClientFallbackFactory implements FallbackFactory<ShopClient> {
    @Override
    public ShopClient create(Throwable cause) {
        return new ShopClient() {
            @Override
            public ShopsInfoVO getShopInfoById(Long shopId) {
                log.error("调用shop服务查询商店基本信息失败:{}",shopId,cause);
                log.error("直接返回null，不影响其他信息的查询");
                return new ShopsInfoVO();
            }

            @Override
            public Map<Long, ShopsInfoVO> getShopInfoByIds(List<Long> shopIds) {
                log.error("调用shop服务查询商店基本信息失败:{}",shopIds,cause);
                log.error("直接返回null，不影响其他信息的查询");
                return null;
            }
        };
    }
}
