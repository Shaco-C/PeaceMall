package com.peachmall.search.service;

import com.peacemall.common.domain.R;

public interface EsProductOperationService {

    //将mysql中的商品数据,批量导入到es中
    R<String> loadProductDocs();

    //创建Product索引
    R<String> createProductIndex();


}
