package com.peacemall.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.mapper.ShopsMapper;
import com.peacemall.shop.service.ShopsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopsServiceImpl extends ServiceImpl<ShopsMapper, Shops> implements ShopsService {
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
        return true;
    }
}
