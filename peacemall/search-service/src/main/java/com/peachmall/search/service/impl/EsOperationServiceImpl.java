package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.utils.CollUtils;
import com.peachmall.search.domain.po.ProductDoc;
import com.peachmall.search.mapper.ProductRepository;
import com.peachmall.search.service.EsOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsOperationServiceImpl implements EsOperationService {

    private final ProductClient productClient;

    private final ProductRepository productRepository;

    private final RestHighLevelClient restHighLevelClient;

    private static final String PRODUCT_INDEX_NAME = "product";


    @Override
    public R<String> loadProductDocs() {
        // 分页查询商品数据
        int size = 1000;

        int pageNo = 1;
        while (true) {
            // 使用我们实现的分页查询方法
            PageDTO<ProductDTO> page = productClient.findAllProductsWithPage(pageNo, size);

            // 非空校验
            List<ProductDTO> products = page.getList();
            if (CollUtils.isEmpty(products)) {
                break;
            }

            log.info("加载第{}页商品数据，共{}条", pageNo, products.size());

            // 转换为文档类型
            List<ProductDoc> productDocs = products.stream()
                    .map(product -> BeanUtil.copyProperties(product, ProductDoc.class))
                    .collect(Collectors.toList());
            log.info("转化为文档类型成功：{}",productDocs);
            // 使用ElasticsearchRepository批量保存
            productRepository.saveAll(productDocs);
            log.info("批量保存文档成功！");
            // 判断是否为最后一页
            if (products.size() < size) {
                log.info("商品数据加载完成！");
                break;
            }

            // 翻页
            pageNo++;
        }
        return R.ok("商品数据加载到es成功");
    }

    @Override
    public R<String> createProductIndex() {
        try {
            GetIndexRequest getRequest = new GetIndexRequest(PRODUCT_INDEX_NAME);
            try {
                // 尝试获取索引（更准确判断存在性）
                restHighLevelClient.indices().get(getRequest, RequestOptions.DEFAULT);
                log.warn("索引 {} 已存在，无需重复创建！", PRODUCT_INDEX_NAME);
                return R.error("索引已存在！");
            } catch (ElasticsearchStatusException e) {
                if (e.status() == RestStatus.NOT_FOUND) {
                    log.info("索引 {} 不存在，开始创建...", PRODUCT_INDEX_NAME);

                    // 创建索引请求
                    CreateIndexRequest createRequest = new CreateIndexRequest(PRODUCT_INDEX_NAME);
                    String mappingJson = "{" +
                            "\"properties\": {" +
                            "    \"productId\": { \"type\": \"long\" }," +
                            "    \"categoryName\": { \"type\": \"keyword\" }," +
                            "    \"brand\": { \"type\": \"keyword\" }," +
                            "    \"name\": { \"type\": \"text\", \"analyzer\": \"ik_smart\" }," +
                            "    \"description\": { \"type\": \"text\", \"analyzer\": \"ik_smart\" }," +
                            "    \"sales\": { \"type\": \"integer\" }" +
                            "}" +
                            "}";
                    createRequest.mapping(mappingJson, XContentType.JSON);

                    // 执行创建
                    restHighLevelClient.indices().create(createRequest, RequestOptions.DEFAULT);
                    log.info("索引 {} 创建成功！", PRODUCT_INDEX_NAME);
                    return R.ok("索引创建成功！");
                } else {
                    log.error("检查索引存在性失败：", e);
                    return R.error("服务异常：" + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("创建索引异常：", e);
            return R.error("索引创建失败：" + e.getMessage());
        }
    }

}
