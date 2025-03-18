package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.peacemall.api.client.ShopClient;
import com.peacemall.common.domain.R;
import com.peachmall.search.domain.po.ShopDoc;
import com.peachmall.search.mapper.ShopRepository;
import com.peachmall.search.service.EsShopOperationService;
import com.peachmall.search.utils.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsShopOperationServiceImpl implements EsShopOperationService {

    private final ShopRepository shopRepository;
    private final ShopClient shopClient;
    private final DataLoader dataLoader;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Override
    public R<String> loadShopDocs() {
        dataLoader.loadData(
                pageNo -> shopClient.findAllShopsWithPage(pageNo, 1000),
                shop -> BeanUtil.copyProperties(shop, ShopDoc.class),
                shopRepository,
                "商店"
        );
        return R.ok("商店数据加载到ES成功");
    }

    @Override
    public R<String> createShopIndex() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(ShopDoc.class);
        return dataLoader.createIndex(indexOps, "商店");
    }
}
