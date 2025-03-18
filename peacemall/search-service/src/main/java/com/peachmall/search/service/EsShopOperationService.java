package com.peachmall.search.service;

import com.peacemall.common.domain.R;

public interface EsShopOperationService {

    //将mysql中的商店数据,批量导入到es中
    R<String> loadShopDocs();

    //创建shop索引
    R<String> createShopIndex();

}
