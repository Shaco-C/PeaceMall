package com.peacemall.api.client;

import com.peacemall.api.client.fallback.ProductClientFallbackFactory;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "product-service" , fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {
    @GetMapping("/products/getProductBasicInfosAndShopInfosById")
    Map<Long, ProductBasicInfosAndShopInfos> getProductBasicInfosAndShopInfosById(@RequestParam List<Long> productIds);

}
