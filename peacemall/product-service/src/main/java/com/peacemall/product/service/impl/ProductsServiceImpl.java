package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.product.domain.po.Products;
import com.peacemall.product.mapper.ProductsMapper;
import com.peacemall.product.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author watergun
 */
@Service
@Slf4j
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {
}
