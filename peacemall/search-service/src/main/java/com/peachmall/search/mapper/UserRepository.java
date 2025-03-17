package com.peachmall.search.mapper;

import com.peachmall.search.domain.po.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<UserDoc, Long> {
}
