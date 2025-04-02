package com.peacemall.shop.domain.vo;


import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.shop.enums.ShopStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ShopPageInfosVO {

    //商店的id
    private Long shopId;

    //商店的名字
    private String shopName;

    //商店的状态
    private ShopStatus shopStatus;

    //商店的描述
    private String shopDescription;

    //商店的创建时间
    private Timestamp createdAt;

    //商店的头像
    private String shopAvatarUrl;

    //商店的商品列表
    private PageDTO<ProductDTO> productPageDTO;
}
