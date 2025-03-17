package com.peachmall.search.service.impl;

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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsSearchServiceImpl implements EsSearchService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ProductClient productClient;


    @Override
    public R<PageDTO<ProductVO>> searchProduct(ProductPageQuery query) {
        log.info("searchProduct: {}", query);
        try {
            // 1. 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 1.1 关键字搜索（在名称和描述中搜索）
            if (StringUtils.hasText(query.getKey())) {
                boolQuery.must(
                        QueryBuilders.boolQuery()
                                .should(QueryBuilders.matchQuery("name", query.getKey()))
                                .should(QueryBuilders.matchQuery("description", query.getKey()))
                );
            }

            // 1.2 分类过滤
            if (StringUtils.hasText(query.getCategory())) {
                boolQuery.filter(QueryBuilders.termQuery("categoryName", query.getCategory()));
            }

            // 1.3 品牌过滤
            if (StringUtils.hasText(query.getBrand())) {
                boolQuery.filter(QueryBuilders.termQuery("brand", query.getBrand()));
            }

            // 2. 构建查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(query.getPageNo() - 1, query.getPageSize()));

            // 3. 排序
            if (StringUtils.hasText(query.getSortBy())) {
                queryBuilder.withSort(
                        SortBuilders.fieldSort(query.getSortBy())
                                .order(query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC)
                );
            } else {
                // 默认按销量排序
                queryBuilder.withSort(SortBuilders.fieldSort("sales").order(SortOrder.DESC));
            }

            // 4. 执行查询
            NativeSearchQuery searchQuery = queryBuilder.build();
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(searchQuery, ProductDoc.class);

            // 5. 解析结果
            List<ProductVO> productVOs = new ArrayList<>();
            for (SearchHit<ProductDoc> hit : searchHits.getSearchHits()) {
                ProductDoc doc = hit.getContent();
                ProductVO vo = new ProductVO();
                vo.setProductId(doc.getProductId());
                vo.setCategoryName(doc.getCategoryName());
                vo.setBrand(doc.getBrand());
                vo.setName(doc.getName());
                vo.setDescription(doc.getDescription());
                vo.setSales(doc.getSales());
                productVOs.add(vo);
            }

            // 6. 构建分页结果
            PageDTO<ProductVO> pageResult = new PageDTO<>();
            pageResult.setTotal(searchHits.getTotalHits());
            pageResult.setPages((long) Math.ceil(searchHits.getTotalHits() * 1.0 / query.getPageSize()));
            pageResult.setList(productVOs);

            return R.ok(pageResult);
        } catch (Exception e) {
            log.error("搜索商品异常", e);
            return R.error("搜索商品失败：" + e.getMessage());
        }
    }

    @Override
    public R<PageDTO<ProductVO>> searchProductsByCategory(Long categoryId, PageQuery query) {
        try {
            // 1. 通过 Feign 调用获取该分类的所有子分类 ID（包括自己）
            List<Long> categoryIds = productClient.getSubCategoryIds(categoryId);
            if (CollectionUtils.isEmpty(categoryIds)) {
                return R.ok(new PageDTO<>());
            }

            // 2. 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termsQuery("categoryId", categoryIds));

            // 3. 构建查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(query.getPageNo() - 1, query.getPageSize()));


            // 5. 执行查询
            SearchHits<ProductDoc> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ProductDoc.class);
            List<ProductVO> productVOs = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        ProductDoc doc = hit.getContent();
                        ProductVO vo = new ProductVO();
                        vo.setProductId(doc.getProductId());
                        vo.setCategoryName(doc.getCategoryName());
                        vo.setBrand(doc.getBrand());
                        vo.setName(doc.getName());
                        vo.setDescription(doc.getDescription());
                        vo.setSales(doc.getSales());
                        return vo;
                    }).collect(Collectors.toList());


            // 7. 构建分页结果
            PageDTO<ProductVO> pageResult = new PageDTO<>();
            pageResult.setTotal(searchHits.getTotalHits());
            pageResult.setPages((searchHits.getTotalHits() + query.getPageSize() - 1) / query.getPageSize());
            pageResult.setList(productVOs);

            return R.ok(pageResult);
        } catch (Exception e) {
            log.error("按分类搜索商品异常", e);
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
                boolQuery.must(
                        QueryBuilders.boolQuery()
                                .should(QueryBuilders.matchQuery("name", key))
                                .should(QueryBuilders.matchQuery("description", key))
                );
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