package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.domain.po.ProductDoc;
import com.peachmall.search.mapper.ProductRepository;
import com.peachmall.search.service.EsProductOperationService;
import com.peachmall.search.utils.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.IndexNotFoundException;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void addProductDocs(ProductDTO productDTO) {
        log.info("添加商品到es中addProductDocs: {}", productDTO);
        try{
            productRepository.save(BeanUtil.copyProperties(productDTO, ProductDoc.class));
        }catch (ElasticsearchException e){
            log.error("商品添加到es中失败: {}", e.getMessage());
            throw new RuntimeException("商品添加到es中失败");
        }
    }

    @Override
    public void deleteProductDocs(List<Long> productIds) {
        log.info("删除商品到es中deleteProductDocs: {}", productIds);
        try{
            productRepository.deleteAllById(productIds);

        }catch (ElasticsearchException e){
            log.error("商品删除到es中失败: {}", e.getMessage());
            throw new RuntimeException("商品删除到es中失败");
        }

    }

    @Override
    public void updateProductDocs(ProductDTO productDTO) {
        log.info("将更新的商品信息为 {} ", productDTO);
        try{
            Optional<ProductDoc> existingProductOpt  = productRepository.findById(productDTO.getProductId());
            if (existingProductOpt.isEmpty()) {
                log.error("商品{} 不存在", productDTO.getProductId());
                //todo 监听时忽略 UserNotFoundException
                throw new DTONotFoundException("商品不存在");
            }
            // 取出原有数据
            ProductDoc existingProduct = existingProductOpt.get();

            BeanUtil.copyProperties(productDTO, existingProduct, CopyOptions.create().setIgnoreNullValue(true));


            // 保存更新后的数据
            productRepository.save(existingProduct);
            log.info("商品 {} 更新成功", productDTO.getProductId());
        }catch (DTONotFoundException e) {
            log.warn("商品不存在，不进行重试: {}", e.getMessage());
        } catch (IndexNotFoundException e) {
            log.error("Elasticsearch 索引不存在，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 索引不存在", e);
        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 存储失败，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 存储失败", e);
        }
    }

}
