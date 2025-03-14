package com.peacemall.api.client.fallback;

import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;
import java.util.Map;

@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public Map<Long, ProductBasicInfosAndShopInfos> getProductBasicInfosAndShopInfosById(List<Long> productIds) {
                log.error("调用商品服务失败");
                throw new RuntimeException("调用商品服务失败");
            }
        };
    }
}
