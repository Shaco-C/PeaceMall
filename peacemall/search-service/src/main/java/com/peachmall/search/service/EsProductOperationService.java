package com.peachmall.search.service;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.ProductDTO;

import java.util.List;

public interface EsProductOperationService {

    //将mysql中的商品数据,批量导入到es中
    R<String> loadProductDocs();

    //创建Product索引
    R<String> createProductIndex();

    //增加商品文档数据
    void addProductDocs(ProductDTO productDTO);
    //删除商品文档数据

    void deleteProductDocs(List<Long> productIds);

    //修改商品文档数据
    void updateProductDocs(ProductDTO productDTO);

}
