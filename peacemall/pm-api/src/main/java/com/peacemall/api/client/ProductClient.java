package com.peacemall.api.client;

import com.peacemall.api.client.fallback.ProductClientFallbackFactory;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.domain.dto.ProductDetailsDTO;
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

    @GetMapping("/products/admin/findAllProductsWithPage")
    PageDTO<ProductDTO> findAllProductsWithPage(@RequestParam(value = "page",defaultValue = "1")int page,
                                                       @RequestParam(value = "pageSize",defaultValue = "1000")int pageSize);

    @GetMapping("/categories/getSubCategoryIds")
    List<Long> getSubCategoryIds(@RequestParam("categoryId") Long categoryId);

    @GetMapping("/products/getProductDetailsByIds")
    Map<Long, ProductDetailsDTO> getProductDetailsByIds(@RequestParam List<Long> productIds,
                                                               @RequestParam List<Long> configIds);
}
