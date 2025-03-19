package com.peachmall.search.service;

import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.ShopDTO;

import java.util.List;

public interface EsShopOperationService {

    //将mysql中的商店数据,批量导入到es中
    R<String> loadShopDocs();

    //创建shop索引
    R<String> createShopIndex();


    //以下的方法都是通过RabbitMQ异步调用的
    //增加商店文档数据
    void addShopDoc(ShopDTO shopDTO);

    //删除商店文档数据
    void deleteShopDoc(List<Long> shopIds);

    //修改商店文档数据
    void updateShopDoc(ShopDTO shopDTO);

}
