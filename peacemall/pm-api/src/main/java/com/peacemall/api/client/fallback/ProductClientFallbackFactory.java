package com.peacemall.api.client.fallback;

import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
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

            @Override
            public PageDTO<ProductDTO> findAllProductsWithPage(int page, int pageSize) {
                log.error("批量获取商品失败");
                log.error("获取的页数为page:{},size:{}",page,pageSize);
                throw new RuntimeException("批量获取商品失败");
            }

            @Override
            public List<Long> getSubCategoryIds(Long categoryId) {
                log.error("获取子分类失败");
                log.error("获取的id为:{}",categoryId);
                throw new RuntimeException("获取子分类失败");
            }
        };
    }
}
