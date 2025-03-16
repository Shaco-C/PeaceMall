package com.peacemall.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.enums.ShopStatus;

import java.util.List;
import java.util.Map;

public interface ShopsService extends IService<Shops> {

    //用户申请成为商家，审批同意之后创建商家
    boolean createUserShop(Shops shops);

    //商家注销商店，商店状态变为CLOSED,用户角色变回USER,在注销前要下架自己的所有商品
    R<String> merchantCloseShop();

    //根据商店id查询商店的基本信息
    //通过feign调用
    ShopsInfoVO getShopInfoById(Long shopId);

    //根据idList查询商店的基本信息
    //用于收藏服务中需要大量加载商品信息以及商店基本信息的场合
    //通过<Long,ShopsInfoVO>的map来返回
    Map<Long, ShopsInfoVO> getShopInfoByIds(List<Long> shopIds);

    //todo 商家查看自己商店的信息
    // R<ShopsDTO> merchantGetShopInfo();

    //todo 商家一键下架所有商品

    //商家更新商店的基础信息
    R<String> merchantUpdateShopInfo(Shops shops);

    //todo 用户点开商店页面之后，显示商店的基本信息以及这个商店的所有商品信息
    //todo 后续需要通过openfeign调用 来获取这个商家的所有商品信息
    //todo 然后聚合在一个ShopDTO中，返回给用户
    //todo 需要考虑的是，如果这个商家没有商品，那么就不需要返回商品信息

    //管理员查看特定状态的商家信息
    R<PageDTO<Shops>> adminGetShopsWithStatus(int page, int pageSize, ShopStatus shopStatus);

    //管理员定期清理状态为CLOSED的商家
    R<String> adminCleanClosedShops();

}
