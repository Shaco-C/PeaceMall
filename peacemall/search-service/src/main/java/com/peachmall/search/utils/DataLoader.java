package com.peachmall.search.utils;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataLoader {

    /**
     * 通用数据加载方法
     * @param pageFetcher  分页数据获取函数
     * @param converter    DTO到文档的转换函数
     * @param repository   ES仓库
     * @param entityName   实体名称（用于日志）
     * @param <T>          DTO类型
     * @param <D>          文档类型
     */
    public <T, D> void loadData(
            Function<Integer, PageDTO<T>> pageFetcher,
            Function<T, D> converter,
            ElasticsearchRepository<D, ?> repository,
            String entityName
    ) {
        int size = 1000;
        int pageNo = 1;

        while (true) {
            PageDTO<T> page = pageFetcher.apply(pageNo);
            List<T> dataList = page.getList();

            if (CollUtils.isEmpty(dataList)) {
                log.info("{}数据加载完成：无更多数据", entityName);
                break;
            }

            log.info("加载第{}页{}数据，共{}条", pageNo, entityName, dataList.size());

            List<D> docs = dataList.stream()
                    .map(converter)
                    .collect(Collectors.toList());

            repository.saveAll(docs);
            log.info("批量保存{}文档成功！", entityName);

            if (dataList.size() < size) {
                log.info("{}数据加载完成！", entityName);
                break;
            }

            pageNo++;
        }
    }

    /**
     * 通用索引创建方法
     * @param indexOps   IndexOperations对象
     * @param indexName  索引名称
     * @return 创建结果
     */
    public R<String> createIndex(IndexOperations indexOps, String indexName) {
        try {
            // 判断索引是否存在
            if (indexOps.exists()) {
                log.warn("索引 {} 已存在，无需重复创建！", indexName);
                return R.error("索引已存在！");
            }

            // 创建索引
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());

            log.info("索引 {} 创建成功！", indexName);
            return R.ok("索引创建成功！");
        } catch (Exception e) {
            log.error("创建索引 {} 失败：", indexName, e);
            return R.error("索引创建失败：" + e.getMessage());
        }
    }


}