package com.peachmall.search.mapper;

import com.peachmall.search.domain.po.ShopDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ShopRepository extends ElasticsearchRepository<ShopDoc, Long> {
}
