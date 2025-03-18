package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.R;
import com.peachmall.search.domain.po.UserDoc;
import com.peachmall.search.mapper.UserRepository;
import com.peachmall.search.service.EsUserOperationService;
import com.peachmall.search.utils.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsUserOperationServiceImpl implements EsUserOperationService {

    private final DataLoader dataLoader;
    private final UserRepository userRepository;
    private final UserClient userClient;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public R<String> loadUserDocs() {
        dataLoader.loadData(
                pageNo -> userClient.findAllUsersWithPage(pageNo, 1000),
                user -> BeanUtil.copyProperties(user, UserDoc.class),
                userRepository,
                "用户"
        );
        return R.ok("用户数据加载到ES成功");
    }

    @Override
    public R<String> createUserIndex() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(UserDoc.class);
        return dataLoader.createIndex(indexOps, "用户");
    }
}
