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
    
    /**
     * 首页热销商品推荐
     * @param 
     * @return 
     * @author watergun
     */
    R<List<ProductVO>> getHotSalesProducts(int topN);

    R<List<ProductVO>> getProductListBySearchAfter(Long lastProductId, int size);
    

    //根据分类查询商品
    R<PageDTO<ProductVO>> searchProductsByCategory(Long categoryId, PageQuery query);

    //用户在搜索结果的页面中，点击品牌选项
    //返回该关键字所有的品牌信息
    //当用户点击具体品牌之后，调用searchProduct，然后添加一个brand属性再去搜索
    R<List<String>> getBrandsBySearchKey(String key);

    //根据用户名，搜索用户

    //根据商店名称搜索商店

}