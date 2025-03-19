package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.peacemall.api.client.ShopClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.ShopDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.domain.po.ShopDoc;
import com.peachmall.search.mapper.ShopRepository;
import com.peachmall.search.service.EsShopOperationService;
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

    @Override
    public void addShopDoc(ShopDTO shopDTO) {
        log.info("将商店 {} 添加到 ES 中", shopDTO);
        try {
            shopRepository.save(BeanUtil.copyProperties(shopDTO, ShopDoc.class));
        }catch (ElasticsearchException e){
            log.error("添加商店到 ES 失败，原因：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 存储商家失败", e);
        }
    }

    @Override
    public void deleteShopDoc(List<Long> shopIds) {
        log.info("将商店 {} 从 ES 中删除", shopIds);
        try {
            shopRepository.deleteAllById(shopIds);

        }catch (ElasticsearchException e){
            log.error("从 ES 删除商店失败，原因：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 删除商家失败", e);
        }

    }

    @Override
    public void updateShopDoc(ShopDTO shopDTO) {
        log.info("将更新的商店信息为 {} ", shopDTO);
        try{
            Optional<ShopDoc> existingShopOpt  = shopRepository.findById(shopDTO.getShopId());
            if (existingShopOpt.isEmpty()) {
                log.error("商店 {} 不存在", shopDTO.getShopId());
                //todo 监听时忽略 ShopNotFoundException
                throw new RuntimeException("商店不存在");
            }
            // 取出原有数据
            ShopDoc existingShop = existingShopOpt.get();
            BeanUtil.copyProperties(shopDTO, existingShop, CopyOptions.create().setIgnoreNullValue(true));
            // 保存更新后的数据
            shopRepository.save(existingShop);
            log.info("商店 {} 更新成功", shopDTO.getShopId());

        }catch (DTONotFoundException e){
            log.error("更新商店信息失败，原因：{}", e.getMessage(), e);

        } catch (IndexNotFoundException e) {
            log.error("Elasticsearch 索引不存在，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 索引不存在", e);
        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 存储失败，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 存储失败", e);
        }

    }
}
