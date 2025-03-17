package com.peachmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.peacemall.api.client.ProductClient;
import com.peacemall.api.client.ShopClient;
import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.domain.dto.ShopDTO;
import com.peacemall.common.domain.dto.UserDTO;
import com.peacemall.common.utils.CollUtils;
import com.peachmall.search.constant.IndexNameConstant;
import com.peachmall.search.domain.po.ProductDoc;
import com.peachmall.search.domain.po.ShopDoc;
import com.peachmall.search.domain.po.UserDoc;
import com.peachmall.search.mapper.ProductRepository;
import com.peachmall.search.mapper.ShopRepository;
import com.peachmall.search.mapper.UserRepository;
import com.peachmall.search.service.EsOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsOperationServiceImpl implements EsOperationService {

    private final ProductClient productClient;
    private final UserClient userClient;
    private final ShopClient shopClient;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    //private final RestHighLevelClient restHighLevelClient;


    @Override
    public R<String> loadProductDocs() {
        loadData(
                pageNo -> productClient.findAllProductsWithPage(pageNo, 1000),
                product -> BeanUtil.copyProperties(product, ProductDoc.class),
                productRepository,
                "商品"
        );
        return R.ok("商品数据加载到ES成功");
    }

    @Override
    public R<String> loadUserDocs() {
        loadData(
                pageNo -> userClient.findAllUsersWithPage(pageNo, 1000),
                user -> BeanUtil.copyProperties(user, UserDoc.class),
                userRepository,
                "用户"
        );
        return R.ok("用户数据加载到ES成功");
    }

    @Override
    public R<String> loadShopDocs() {
        loadData(
                pageNo -> shopClient.findAllShopsWithPage(pageNo, 1000),
                shop -> BeanUtil.copyProperties(shop, ShopDoc.class),
                shopRepository,
                "商店"
        );
        return R.ok("商店数据加载到ES成功");
    }

    @Override
    public R<String> createProductIndex() {
        return createIndex(ProductDoc.class, IndexNameConstant.PRODUCT_INDEX_NAME);
    }

    @Override
    public R<String> createUserIndex() {
        return createIndex(UserDoc.class, IndexNameConstant.USER_INDEX_NAME);
    }

    @Override
    public R<String> createShopIndex() {
        return createIndex(ShopDoc.class, IndexNameConstant.SHOP_INDEX_NAME);
    }

    private <T> R<String> createIndex(Class<T> docClass, String indexName) {
        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(docClass);

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
    private  <T, D> void loadData(
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

//    @Override
//    public R<String> createProductIndex() {
//        try {
//            GetIndexRequest getRequest = new GetIndexRequest(PRODUCT_INDEX_NAME);
//            try {
//                // 尝试获取索引（更准确判断存在性）
//                restHighLevelClient.indices().get(getRequest, RequestOptions.DEFAULT);
//                log.warn("索引 {} 已存在，无需重复创建！", PRODUCT_INDEX_NAME);
//                return R.error("索引已存在！");
//            } catch (ElasticsearchStatusException e) {
//                if (e.status() == RestStatus.NOT_FOUND) {
//                    log.info("索引 {} 不存在，开始创建...", PRODUCT_INDEX_NAME);
//
//                    // 创建索引请求
//                    CreateIndexRequest createRequest = new CreateIndexRequest(PRODUCT_INDEX_NAME);
//                    String mappingJson = "{" +
//                            "\"properties\": {" +
//                            "    \"productId\": { \"type\": \"long\" }," +
//                            "    \"categoryId\": { \"type\": \"keyword\" }," +
//                            "    \"categoryName\": { \"type\": \"keyword\" }," +
//                            "    \"brand\": { \"type\": \"keyword\" }," +
//                            "    \"name\": { \"type\": \"text\", \"analyzer\": \"ik_smart\" }," +
//                            "    \"description\": { \"type\": \"text\", \"analyzer\": \"ik_smart\" }," +
//                            "    \"sales\": { \"type\": \"integer\" }" +
//                            "}" +
//                            "}";
//
//                    createRequest.mapping(mappingJson, XContentType.JSON);
//
//                    // 执行创建
//                    restHighLevelClient.indices().create(createRequest, RequestOptions.DEFAULT);
//                    log.info("索引 {} 创建成功！", PRODUCT_INDEX_NAME);
//                    return R.ok("索引创建成功！");
//                } else {
//                    log.error("检查索引存在性失败：", e);
//                    return R.error("服务异常：" + e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            log.error("创建索引异常：", e);
//            return R.error("索引创建失败：" + e.getMessage());
//        }
//    }

}
