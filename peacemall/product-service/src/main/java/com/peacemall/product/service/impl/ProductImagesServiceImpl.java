package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.product.domain.po.ProductImages;
import com.peacemall.product.mapper.ProductImagesMapper;
import com.peacemall.product.service.ProductImagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author watergun
 */
@Service
@Slf4j
public class ProductImagesServiceImpl extends ServiceImpl<ProductImagesMapper, ProductImages> implements ProductImagesService {
}
