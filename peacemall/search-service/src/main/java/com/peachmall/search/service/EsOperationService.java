package com.peachmall.search.service;

import com.peacemall.common.domain.R;

public interface EsOperationService {

    //将mysql中的商品数据,批量导入到es中
    R<String> loadProductDocs();

    //将mysql中的用户数据,批量导入到es中
    R<String> loadUserDocs();

    //将mysql中的商店数据,批量导入到es中
    R<String> loadShopDocs();

    //创建Product索引
    R<String> createProductIndex();

    //创建user索引
    R<String> createUserIndex();

    //创建shop索引
    R<String> createShopIndex();
}
