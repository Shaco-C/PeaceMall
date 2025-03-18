package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.R;
import com.peachmall.search.domain.po.ProductDoc;
import com.peachmall.search.mapper.ProductRepository;
import com.peachmall.search.service.EsProductOperationService;
import com.peachmall.search.utils.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsProductOperationServiceImpl implements EsProductOperationService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ProductRepository productRepository;
    private final ProductClient productClient;
    private final DataLoader dataLoader;

    @Override
    public R<String> loadProductDocs() {
        dataLoader.loadData(
                pageNo -> productClient.findAllProductsWithPage(pageNo, 1000),
                product -> BeanUtil.copyProperties(product, ProductDoc.class),
                productRepository,
                "商品"
        );
        return R.ok("商品数据加载到ES成功");
    }

    @Override
    public R<String> createProductIndex() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(ProductDoc.class);
        return dataLoader.createIndex(indexOps, "商品");
    }
}
