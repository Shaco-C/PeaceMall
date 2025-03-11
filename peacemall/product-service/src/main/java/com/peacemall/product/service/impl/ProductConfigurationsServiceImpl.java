package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.mapper.ProductConfigurationsMapper;
import com.peacemall.product.service.ProductConfigurationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author watergun
 */
@Service
@Slf4j
public class ProductConfigurationsServiceImpl extends ServiceImpl<ProductConfigurationsMapper, ProductConfigurations> implements ProductConfigurationsService {

}
