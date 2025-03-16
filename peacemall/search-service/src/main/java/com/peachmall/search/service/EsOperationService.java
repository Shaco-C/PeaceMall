package com.peachmall.search.service;

import com.peacemall.common.domain.R;

public interface EsOperationService {

    //将mysql中的数据,批量导入到es中
    R<String> loadProductDocs();

    //创建Product索引
    R<String> createProductIndex();
}
