package com.peacemall.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ShopDTO;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.domain.vo.ShopPageInfosVO;
import com.peacemall.shop.enums.ShopStatus;
import com.peacemall.shop.service.ShopsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("商家商店服务相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopsController {

    private final ShopsService shopsService;

    //商家注销商店，商店状态变为CLOSED,用户角色变回USER,在注销前要下架自己的所有商品
    @ApiOperation("商家注销商店")
    @PutMapping("/merchant/close")
    public R<String> merchantCloseShop(){
        return shopsService.merchantCloseShop();
    }

    //todo 商家查看自己商店的信息
    // R<ShopsDTO> merchantGetShopInfo();

    //商家更新商店的基础信息
    @ApiOperation("商家更新商店的基础信息")
    @PutMapping("/merchant/update")
    public R<String> merchantUpdateShopInfo(@RequestBody Shops shops){
        return shopsService.merchantUpdateShopInfo(shops);
    }

    // 用户点开商店页面之后，显示商店的基本信息以及这个商店的所有商品信息
    // 后续需要通过openfeign调用 来获取这个商家的所有商品信息
    // 然后聚合在一个ShopDTO中，返回给用户
    // 需要考虑的是，如果这个商家没有商品，那么就不需要返回商品信息
    @ApiOperation("用户点开商店页面之后，显示商店的基本信息以及这个商店的所有商品信息")
    @GetMapping("/getShopPageInfoByShopId")
    public R<ShopPageInfosVO> getShopPageInfoByShopId(@RequestParam(value = "page",defaultValue = "1") int page,
                                                      @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                                      @RequestParam("shopId") Long shopId){
        return shopsService.getShopPageInfoByShopId(page,pageSize,shopId);
    }

    //管理员查看特定状态的商家信息
    @ApiOperation("管理员查看特定状态的商家信息")
    @GetMapping("/admin/getShopsWithStatus")
    public R<PageDTO<Shops>> adminGetShopsWithStatus(@RequestParam(value = "page",defaultValue = "1") int page,
                                                     @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                                     @RequestParam(value = "shopStatus",defaultValue = "CLOSED") ShopStatus shopStatus){
        return shopsService.adminGetShopsWithStatus(page,pageSize,shopStatus);
    }

    //管理员定期清理状态为CLOSED的商家
    @ApiOperation("管理员定期清理状态为CLOSED的商家")
    @DeleteMapping("/admin/cleanClosedShops")
    public R<String> adminCleanClosedShops(){
        return shopsService.adminCleanClosedShops();
    }

    //根据商店id查询商店的基本信息
    //通过feign调用
    @ApiOperation("根据商店id查询商店的基本信息")
    @GetMapping("/getShopInfoById")
    public ShopsInfoVO getShopInfoById(@RequestParam("shopId") Long shopId){
        return shopsService.getShopInfoById(shopId);
    }

    //根据idList查询商店的基本信息
    //用于收藏服务中需要大量加载商品信息以及商店基本信息的场合
    //通过<Long,ShopsInfoVO>的map来返回
    @ApiOperation("根据idList查询商店的基本信息")
    @GetMapping("/getShopInfoByIdsList")
    public Map<Long, ShopsInfoVO> getShopInfoByIds(@RequestParam List<Long> shopIds){
        return shopsService.getShopInfoByIds(shopIds);
    }

    //分页查询所有的商家信息
    //用于es数据的批量插入
    @ApiOperation(value = "分页查询所有的商品信息")
    @GetMapping("/admin/findAllShopsWithPage")
    public PageDTO<ShopDTO> findAllShopsWithPage(@RequestParam(value = "page",defaultValue = "1")int page,
                                                 @RequestParam(value = "pageSize",defaultValue = "1000")int pageSize) {
        return shopsService.findAllShopsWithPage(page,pageSize);
    }

}
