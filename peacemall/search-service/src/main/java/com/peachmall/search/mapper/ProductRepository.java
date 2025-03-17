package com.peachmall.search.mapper;

import com.peachmall.search.domain.po.ProductDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ProductRepository extends ElasticsearchRepository<ProductDoc, Long> {

}
