package com.peacemall.api.client;

import com.peacemall.api.client.fallback.ShopClientFallbackFactory;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "shop-service" , fallbackFactory = ShopClientFallbackFactory.class)
public interface ShopClient {
    @GetMapping("/shops/getShopInfoById")
    ShopsInfoVO getShopInfoById(@RequestParam("shopId") Long shopId);

    @GetMapping("/shops/getShopInfoByIdsList")
    Map<Long, ShopsInfoVO> getShopInfoByIds(@RequestParam List<Long> shopIds);
}
