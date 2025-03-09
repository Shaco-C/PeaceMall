package com.peacemall.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.shop.domain.po.Shops;
import com.peacemall.shop.mapper.ShopsMapper;
import com.peacemall.shop.service.ShopsService;
import org.springframework.stereotype.Service;

@Service
public class ShopsServiceImpl extends ServiceImpl<ShopsMapper, Shops> implements ShopsService {
}
