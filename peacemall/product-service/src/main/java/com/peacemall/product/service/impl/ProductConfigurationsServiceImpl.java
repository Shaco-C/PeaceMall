package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.dto.ProductConfigDTO;
import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.mapper.ProductConfigurationsMapper;
import com.peacemall.product.service.ProductConfigurationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author watergun
 */
@Service
@Slf4j
public class ProductConfigurationsServiceImpl extends ServiceImpl<ProductConfigurationsMapper, ProductConfigurations> implements ProductConfigurationsService {

    //创建商品配置
    @Override
    public R<String> merchantCreateProductConfigurations(ProductConfigDTO productConfigDTO) {
        log.info("创建商品配置:{}", productConfigDTO);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();

        if (userId==null || !UserRole.MERCHANT.name().equals(userRole)){
            log.info("用户没有权限创建商品配置");
            return R.error("用户没有权限创建商品配置");
        }

        log.info("用户有权限创建商品配置");

        ProductConfigurations productConfigurations = new ProductConfigurations();
        BeanUtils.copyProperties(productConfigDTO,productConfigurations);

        boolean save = this.save(productConfigurations);
        if (!save){
            log.error("创建商品配置失败");
            return R.error("创建商品配置失败");
        }
        log.info("创建商品配置成功");
        return R.ok("创建商品配置成功");
    }

    //删除商品配置
    @Override
    @Transactional
    public R<String> merchantDeleteProductConfigurations(List<Long> configurationsIdsList) {
        log.info("删除商品配置:{}", configurationsIdsList);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId==null || !UserRole.MERCHANT.name().equals(userRole)){
            log.info("用户没有权限删除商品配置");
            return R.error("用户没有权限删除商品配置");
        }
        log.info("用户有权限删除商品配置");
        boolean remove = this.removeByIds(configurationsIdsList);
        if (!remove){
            log.error("删除商品配置失败");
            return R.error("删除商品配置失败");
        }
        log.info("删除商品配置成功");
        return R.ok("删除商品配置成功");
    }


    //修改商品配置
    @Override
    public R<String> merchantUpdateProductConfigurations(ProductConfigurations productConfigurations) {
        log.info("修改商品配置:{}", productConfigurations);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId==null || !UserRole.MERCHANT.name().equals(userRole)){
            log.info("用户没有权限修改商品配置");
            return R.error("用户没有权限修改商品配置");
        }
        log.info("用户有权限修改商品配置");
        boolean update = this.updateById(productConfigurations);
        if (!update){
            log.error("修改商品配置失败");
            return R.error("修改商品配置失败");
        }
        log.info("修改商品配置成功");
        return R.ok("修改商品配置成功");
    }

    //根据商品id删除所有商品配置
    @Override
    @Transactional
    public void merchantDeleteProductConfigurationsByProductId(Long productId) {
        log.info("根据商品id删除所有商品配置:{}", productId);
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId==null || !UserRole.MERCHANT.name().equals(userRole)){
            log.info("用户没有权限删除商品配置");
            throw new RuntimeException("用户没有权限删除商品配置");
        }
        log.info("用户有权限删除商品配置");

        LambdaQueryWrapper<ProductConfigurations> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductConfigurations::getProductId, productId);

        List<Long> configIdsList = this.list(queryWrapper).stream().map(ProductConfigurations::getConfigId).collect(Collectors.toList());
        if (configIdsList.isEmpty()){
            log.error("商品配置不存在");
            //不需要继续执行了
            return;
        }
        boolean remove = this.removeByIds(configIdsList);
        if (!remove){
            log.error("删除商品配置失败");
            throw new RuntimeException("删除商品配置失败");
        }
        log.info("删除商品配置成功");
    }

    //根据商品id查询商品配置
    @Override
    public List<ProductConfigurations> queryProductConfigurationsByProductId(Long productId) {
        log.info("根据商品id查询商品配置:{}", productId);

        LambdaQueryWrapper<ProductConfigurations> productConfigurationsLambdaQueryWrapper =new LambdaQueryWrapper<>();
        productConfigurationsLambdaQueryWrapper.eq(ProductConfigurations::getProductId, productId);
        return this.list(productConfigurationsLambdaQueryWrapper);
    }
}
