package com.peachmall.search.service.impl;

import cn.hutool.core.util.StrUtil;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.query.PageQuery;
import com.peachmall.search.domain.po.ProductDoc;
import com.peachmall.search.domain.query.ProductPageQuery;
import com.peachmall.search.domain.vo.ProductVO;
import com.peachmall.search.service.EsSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsSearchServiceImpl implements EsSearchService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ProductClient productClient;
    private final RestHighLevelClient restHighLevelClient;

    @Override
    public R<PageDTO<ProductVO>> searchProduct(ProductPageQuery query) {
        log.info("searchProduct: {}", query);
        try {
            // 1. 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (StringUtils.hasText(query.getKey())) {
                // 1.1 关键字搜索（匹配 name 和 description）
                boolQuery.should(QueryBuilders.matchQuery("name", query.getKey()).boost(2.0f)); // 提高 name 字段的权重
                boolQuery.should(QueryBuilders.matchQuery("description", query.getKey()));

                // 1.2 分类和品牌匹配
                boolQuery.should(QueryBuilders.termQuery("categoryName", query.getKey()));
                boolQuery.should(QueryBuilders.termQuery("brand", query.getKey()));

                // 让至少一个条件匹配（否则不返回结果）
                boolQuery.minimumShouldMatch(1);
            }
            if (StringUtils.hasText(query.getBrand())) {
                boolQuery.filter(QueryBuilders.termQuery("brand", query.getBrand()));
            }

            // 2. 构建查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(query.getPageNo() - 1, query.getPageSize()));

            // 3. 排序逻辑优化
            String sortBy = StrUtil.isEmpty(query.getSortBy()) ? "sales" : query.getSortBy();
            SortOrder order = Boolean.TRUE.equals(query.getIsAsc()) ? SortOrder.ASC : SortOrder.DESC;
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(order));

            // 4. 执行查询
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ProductDoc.class);

            // 5. 解析结果
            List<ProductVO> productVOs = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        ProductDoc doc = hit.getContent();
                        return new ProductVO(
                                doc.getProductId(),
                                doc.getCategoryId(),
                                doc.getCategoryName(),
                                doc.getBrand(),
                                doc.getName(),
                                doc.getPrice(),  // ⚠️ 这里调整了 price 的顺序
                                doc.getDescription(),
                                doc.getSales(),
                                doc.getImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            // 6. 构建分页结果
            PageDTO<ProductVO> pageResult = new PageDTO<>(searchHits.getTotalHits(),
                    (long) Math.ceil(searchHits.getTotalHits() * 1.0 / query.getPageSize()), productVOs);

            return R.ok(pageResult);
        } catch (Exception e) {
            log.error("搜索商品异常", e);
            return R.error("搜索商品失败：" + e.getMessage());
        }
    }

    public R<List<ProductVO>> getHotSalesProducts(int topN) {
        if (topN <= 0) {
            return R.error("查询数量必须大于 0");
        }

        try {
            // 1. 构建查询：查询所有有销量记录的商品，并按销量排序
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("sales"))) // 只查询有销量的商品
                    .withSort(SortBuilders.fieldSort("sales").order(SortOrder.DESC)) // 按销量降序
                    .withPageable(PageRequest.of(0, topN)) // 取前 topN 条
                    .build();

            // 2. 执行查询
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(searchQuery, ProductDoc.class);

            // 3. 解析结果
            List<ProductVO> productVOs = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        ProductDoc doc = hit.getContent();
                        return new ProductVO(
                                doc.getProductId(),
                                doc.getCategoryId(),
                                doc.getCategoryName(),
                                doc.getBrand(),
                                doc.getName(),
                                doc.getPrice(),
                                doc.getDescription(),
                                doc.getSales(),
                                doc.getImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return R.ok(productVOs);
        } catch (Exception e) {
            log.error("获取热销商品失败, topN: {}", topN, e);
            return R.error("获取热销商品失败：" + e.getMessage());
        }
    }

    @Override
    public R<List<ProductVO>> getProductListBySearchAfter(Long lastProductId, int size) {
        if (size <= 0) {
            return R.error("查询数量必须大于 0");
        }

        try {
            // 1. 构建查询，默认查询所有商品
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchAllQuery())  // 查询所有商品
                    .sort(SortBuilders.fieldSort("productId").order(SortOrder.ASC)) // 按 productId 升序
                    .size(size);  // 设置每次加载的数据量

            // 2. 使用 search_after 进行深度分页
            if (lastProductId != null) {
                sourceBuilder.searchAfter(new Object[]{lastProductId});
            }

            // 3. 创建 SearchRequest
            SearchRequest searchRequest = new SearchRequest("product");
            searchRequest.source(sourceBuilder);

            // 4. 执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 5. 解析查询结果
            List<ProductVO> productVOs = Arrays.stream(searchResponse.getHits().getHits())
                    .map(hit -> {
                        Map<String, Object> sourceMap = hit.getSourceAsMap();
                        return new ProductVO(
                                Long.valueOf(sourceMap.get("productId").toString()),
                                Long.valueOf(sourceMap.get("categoryId").toString()),
                                sourceMap.get("categoryName").toString(),
                                sourceMap.get("brand").toString(),
                                sourceMap.get("name").toString(),
                                new BigDecimal(sourceMap.get("price").toString()),
                                sourceMap.get("description").toString(),
                                Integer.parseInt(sourceMap.get("sales").toString()),
                                sourceMap.get("imageUrl").toString()
                        );
                    }).collect(Collectors.toList());

            return R.ok(productVOs);
        } catch (Exception e) {
            log.error("使用 search_after 加载商品异常", e);
            return R.error("加载商品失败：" + e.getMessage());
        }
    }


    @Override
    public R<PageDTO<ProductVO>> searchProductsByCategory(Long categoryId, PageQuery query) {
        try {
            // 1. 通过 Feign 获取该分类的所有子分类 ID（包括自身）
            List<Long> categoryIds = productClient.getSubCategoryIds(categoryId);
            if (CollectionUtils.isEmpty(categoryIds)) {
                return R.ok(new PageDTO<>());
            }

            // 2. 构建查询条件（按分类 ID 过滤）
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termsQuery("categoryId", categoryIds));

            // 3. 构建查询
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(query.getPageNo() - 1, query.getPageSize()))
                    .build();

            // 4. 执行查询
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(searchQuery, ProductDoc.class);

            // 5. 解析结果
            List<ProductVO> productVOs = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        ProductDoc doc = hit.getContent();
                        return new ProductVO(
                                doc.getProductId(),
                                doc.getCategoryId(),
                                doc.getCategoryName(),
                                doc.getBrand(),
                                doc.getName(),
                                doc.getPrice(),
                                doc.getDescription(),
                                doc.getSales(),
                                doc.getImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            // 6. 计算总页数
            long totalHits = searchHits.getTotalHits();
            long totalPages = (totalHits + query.getPageSize() - 1) / query.getPageSize();

            // 7. 构建分页结果
            PageDTO<ProductVO> pageResult = new PageDTO<>(totalHits, totalPages, productVOs);

            return R.ok(pageResult);
        } catch (Exception e) {
            log.error("按分类搜索商品异常, categoryId: {}, query: {}", categoryId, query, e);
            return R.error("按分类搜索商品失败：" + e.getMessage());
        }
    }
    @Override
    public R<List<String>> getBrandsBySearchKey(String key) {
        try {
            // 1. 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 1.1 关键字搜索（在名称和描述中搜索）
            if (StringUtils.hasText(key)) {
                // 1.1 关键字搜索（匹配 name 和 description）
                boolQuery.should(QueryBuilders.matchQuery("name", key).boost(2.0f)); // 提高 name 字段的权重
                boolQuery.should(QueryBuilders.matchQuery("description", key));

                // 1.2 分类和品牌匹配
                boolQuery.should(QueryBuilders.termQuery("categoryName", key));
                boolQuery.should(QueryBuilders.termQuery("brand", key));

                // 让至少一个条件匹配（否则不返回结果）
                boolQuery.minimumShouldMatch(1);
            }

            // 2. 只执行品牌聚合，不返回文档
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .addAggregation(AggregationBuilders.terms("brands").field("brand").size(50)) // 获取前 50 个不同品牌
                    .withMaxResults(0) // 只返回聚合，不返回文档
                    .build();

            // 3. 执行查询
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(query, ProductDoc.class);

            // 4. 解析品牌聚合
            List<String> brands = new ArrayList<>();
            Aggregations aggregations = searchHits.getAggregations();
            if (aggregations != null) {
                Terms brandTerms = aggregations.get("brands");
                if (brandTerms != null) {
                    brands = brandTerms.getBuckets()
                            .stream()
                            .map(Terms.Bucket::getKeyAsString)
                            .collect(Collectors.toList());
                }
            }

            return R.ok(brands);
        } catch (Exception e) {
            log.error("获取品牌列表异常", e);
            return R.error("获取品牌列表失败：" + e.getMessage());
        }
    }
    
}