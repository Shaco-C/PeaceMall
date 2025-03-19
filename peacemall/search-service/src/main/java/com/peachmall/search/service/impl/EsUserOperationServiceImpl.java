package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.UserDTO;
import com.peacemall.common.exception.DTONotFoundException;
import com.peachmall.search.domain.po.UserDoc;
import com.peachmall.search.mapper.UserRepository;
import com.peachmall.search.service.EsUserOperationService;
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

    @Override
    public void addUserDoc(UserDTO userDTO) {

        log.info("将用户 {} 添加到 ES 中", userDTO);
        try {
            userRepository.save(BeanUtil.copyProperties(userDTO, UserDoc.class));
        } catch (ElasticsearchException e) {
            log.error("添加用户到 ES 失败，原因：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 存储失败", e);
        }
    }


    @Override
    public void deleteUserDoc(List<Long> userId) {
        log.info("将用户 {} 从 ES 中删除", userId);
        try {
            userRepository.deleteAllById(userId);
        } catch (ElasticsearchException e) {
            log.error("从 ES 删除用户失败，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 删除失败", e);
        }
    }


    @Override
    public void updateUserDoc(UserDTO userDTO) {
        log.info("将更新的用户信息为 {} ", userDTO);
        try{
            Optional<UserDoc> existingUserOpt  = userRepository.findById(userDTO.getUserId());
            if (existingUserOpt.isEmpty()) {
                log.error("用户 {} 不存在", userDTO.getUserId());
                //todo 监听时忽略 UserNotFoundException
                throw new DTONotFoundException("用户不存在");
            }
            // 取出原有数据
            UserDoc existingUser = existingUserOpt.get();

            BeanUtil.copyProperties(userDTO, existingUser, CopyOptions.create().setIgnoreNullValue(true));


            // 保存更新后的数据
            userRepository.save(existingUser);
            log.info("用户 {} 更新成功", userDTO.getUserId());
        }catch (DTONotFoundException e) {
            log.warn("用户不存在，不进行重试: {}", e.getMessage());
        } catch (IndexNotFoundException e) {
            log.error("Elasticsearch 索引不存在，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 索引不存在", e);
        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 存储失败，错误：{}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 存储失败", e);
        }
    }

}
