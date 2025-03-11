package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.product.domain.po.Categories;
import com.peacemall.product.mapper.CategoriesMapper;
import com.peacemall.product.service.CategoriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author watergun
 */
@Service
@Slf4j
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {
}
