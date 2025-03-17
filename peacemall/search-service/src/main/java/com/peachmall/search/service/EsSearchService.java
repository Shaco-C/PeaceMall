package com.peachmall.search.service;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.query.PageQuery;
import com.peachmall.search.domain.query.ProductPageQuery;
import com.peachmall.search.domain.vo.ProductVO;

import java.util.List;

public interface EsSearchService {
    /**
     * 搜索商品
     * @param query 查询条件
     * @return 分页结果
     */
    R<PageDTO<ProductVO>> searchProduct(ProductPageQuery query);

    R<PageDTO<ProductVO>> searchProductsByCategory(Long categoryId, PageQuery query);

    R<List<String>> getBrandsBySearchKey(String key);

}