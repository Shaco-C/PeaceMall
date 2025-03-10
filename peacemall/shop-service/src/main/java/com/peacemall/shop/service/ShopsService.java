package com.peacemall.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.enums.ShopStatus;

public interface ShopsService extends IService<Shops> {

    //用户申请成为商家，审批同意之后创建商家
    boolean createUserShop(Shops shops);

    //商家注销商店，商店状态变为CLOSED,用户角色变回USER,在注销前要下架自己的所有商品
    R<String> merchantCloseShop();

    //todo 商家查看自己商店的信息
    // R<ShopsDTO> merchantGetShopInfo();

    //商家更新商店的基础信息
    R<String> merchantUpdateShopInfo(Shops shops);

    //todo 用户点开商店页面之后，显示商店的基本信息以及这个商店的所有商品信息
    //todo 后续需要通过openfeign调用 来获取这个商家的所有商品信息
    //todo 然后聚合在一个ShopDTO中，返回给用户
    //todo 需要考虑的是，如果这个商家没有商品，那么就不需要返回商品信息

    //管理员查看特定状态的商家信息
    R<Page<Shops>> adminGetShopsWithStatus(int page, int pageSize, ShopStatus shopStatus);

    //管理员定期清理状态为CLOSED的商家
    R<String> adminCleanClosedShops();

}
