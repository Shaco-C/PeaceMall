package com.peachmall.search.service;

import com.peacemall.common.domain.R;

public interface EsUserOperationService {

    //将mysql中的用户数据,批量导入到es中
    R<String> loadUserDocs();

    //创建user索引
    R<String> createUserIndex();
}
