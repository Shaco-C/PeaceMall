package com.peacemall.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.enums.ShopStatus;
import com.peacemall.shop.mapper.ShopsMapper;
import com.peacemall.shop.service.ShopsService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopsServiceImpl extends ServiceImpl<ShopsMapper, Shops> implements ShopsService {

    private final UserClient userClient;
    @Override
    public boolean createUserShop(Shops shops) {
        log.info("createUserShop method is called,shops:{}", shops);

        if (shops == null) {
            log.error("createUserShop method failed,shops is null");
            return false;
        }
        log.info("shops正常");

        boolean save = this.save(shops);
        if (!save) {
            log.error("createUserShop method failed,shops save failed");
            return false;
        }
        log.info("createUserShop method success,shops:{}", shops);
        return true;
    }

    @Override
    @GlobalTransactional
    public R<String> merchantCloseShop() {
        log.info("merchantCloseShop method is called");

        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("merchantCloseShop method,userId:{},userRole:{}", userId, userRole);

        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录，或用户不是商家");
            return R.error("用户未登录，或用户不是商家");
        }

        log.info("用户已登录，且是商家");

        //查看商家商店是否存在
        Shops shops = this.lambdaQuery().eq(Shops::getUserId, userId).one();

        //如果不存在
        if (shops == null) {
            log.error("商家商店不存在");
            return R.error("商家商店不存在,请刷新重试");
        }
        log.info("商家商店存在");

        //todo 如果存在，检查商家商店下是否有还是上架的商品
        // 如果存在未下架商品，则无法注销商店

        //如果不存在未下架商品，则先将将用户角色变为User
        try{
            userClient.adminChangeUserRole(userId, UserRole.USER);
        }catch (Exception e){
            log.error("用户身份修改失败");

            throw new RuntimeException("用户身份修改失败");
        }
        log.info("用户身份修改成功");
        //用户角色改变顺利
        //改变商店状态
        shops.setShopStatus(ShopStatus.CLOSED);
        //提交更新
        boolean updated = this.updateById(shops);
        if (!updated) {
            log.error("商店状态修改失败");
            throw new RuntimeException("商店状态修改失败");
        }
        log.info("商店状态修改成功");
        return R.ok("商店注销成功");
    }

    @Override
    public ShopsInfoVO getShopInfoById(Long shopId) {
        log.info("getShopInfoById method is called,shopId:{}", shopId);
        if (shopId == null) {
            log.error("getShopInfoById method failed,shopId is null");
            return null;
        }
        log.info("shopId正常");
        Shops shops = this.getById(shopId);
        if (shops == null) {
            log.error("getShopInfoById method failed,shopId is not exist");
            return null;
        }
        log.info("shopId存在");
        ShopsInfoVO shopsInfoVO = new ShopsInfoVO();
        BeanUtil.copyProperties(shops,shopsInfoVO);
        log.info("shopsInfoVO:{}", shopsInfoVO);
        return shopsInfoVO;
    }

    @Override
    public Map<Long, ShopsInfoVO> getShopInfoByIds(List<Long> shopIds) {
        log.info("getShopInfoByIds method is called, shopIds: {}", shopIds);

        if (BeanUtil.isEmpty(shopIds)) {
            log.error("getShopInfoByIds method failed: shopIds is null or empty");
            throw new IllegalArgumentException("shopIds cannot be null or empty");
        }

        List<Shops> shops = this.lambdaQuery().in(Shops::getShopId, shopIds).list();
        if (BeanUtil.isEmpty(shops)) {
            log.error("getShopInfoByIds method failed: shopIds do not exist");
            throw new IllegalArgumentException("shopIds do not exist");
        }

        //通过商家id与商家信息进行绑定
        ShopsInfoVO shopsInfoVO = new ShopsInfoVO();
        Map<Long, ShopsInfoVO> shopsInfoVOMap = shops.stream()
                .collect(Collectors.toMap(Shops::getShopId, shop -> {
                    BeanUtil.copyProperties(shop, shopsInfoVO);
                    return shopsInfoVO;
                }));

        log.info("Successfully retrieved shop information: {}", shopsInfoVOMap);
        return shopsInfoVOMap;
    }


    @Override
    public R<String> merchantUpdateShopInfo(Shops shops) {
        log.info("merchantUpdateShopInfo method is called");
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("merchantUpdateShopInfo method,userId:{},userRole:{}", userId, userRole);

        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录，或用户不是商家");
            return R.error("用户未登录，或用户不是商家");
        }
        log.info("用户已登录，且是商家");
        //检查用户是否修改商店的status字段
        if (shops.getShopStatus() != null) {
            log.error("用户不能修改商店状态");
            return R.error("用户不能修改商店状态");
        }
        //查看商家商店是否存在
        Shops currentShop = this.lambdaQuery().eq(Shops::getUserId, userId).one();
        if (currentShop == null) {
            log.error("商家商店不存在");
            return R.error("商家商店不存在,请刷新重试");
        }
        log.info("商家商店存在");
        //更新商店信息
        shops.setShopId(currentShop.getShopId());
        log.info("更新商店信息:{}", shops);
        boolean updated = this.updateById(shops);
        if (!updated) {
            log.error("商店信息修改失败");
            return R.error("商店信息修改失败");
        }
        log.info("商店信息修改成功");
        return R.ok("商店信息修改成功");
    }

    @Override
    public R<Page<Shops>> adminGetShopsWithStatus(int page, int pageSize, ShopStatus shopStatus) {
        log.info("adminGetShopsWithStatus method is called");
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("adminGetShopsWithStatus method,userId:{},userRole:{}", userId, userRole);
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录，或用户不是管理员");
            return R.error("用户未登录，或用户不是管理员");
        }
        log.info("用户已登录，且是管理员");
        //查看商家商店信息
        Page<Shops> shopsPage = new Page<>(page, pageSize);
        this.page(shopsPage, new LambdaQueryWrapper<Shops>().eq(Shops::getShopStatus, shopStatus));
        return R.ok(shopsPage);
    }

    @Override
    @Transactional
    public R<String> adminCleanClosedShops() {
        log.info("adminCleanClosedShops method is called");
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("adminCleanClosedShops method,userId:{},userRole:{}", userId, userRole);
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录，或用户不是管理员");
            return R.error("用户未登录，或用户不是管理员");
        }
        log.info("用户已登录，且是管理员");
        //查看所有状态为CLOSED的商店
        List<Long> shopsIdList = this.lambdaQuery().eq(Shops::getShopStatus, ShopStatus.CLOSED).list()
                .stream().map(Shops::getShopId).collect(Collectors.toList());

        if (shopsIdList.isEmpty()) {
            log.info("没有状态为CLOSED的商店");
            return R.ok("没有状态为CLOSED的商店");
        }
        log.info("存在状态为CLOSED的商店");
        //删除所有状态为CLOSED的商店
        boolean deleted = this.removeByIds(shopsIdList);
        if (!deleted) {
            log.error("商店删除失败");
            throw new RuntimeException("商店删除失败");
        }
        log.info("商店删除成功");
        return R.ok("商店删除成功,删除了"+shopsIdList.size()+"个CLOSED商店");
    }
}
