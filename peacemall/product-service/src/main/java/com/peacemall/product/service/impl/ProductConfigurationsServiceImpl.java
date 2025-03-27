package com.peacemall.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.constant.StockChangeLogMQConstant;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.StockChangeLogDTO;
import com.peacemall.common.enums.StockSourceType;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.RabbitMqHelper;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.dto.ProductConfigDTO;
import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.mapper.ProductConfigurationsMapper;
import com.peacemall.product.service.ProductConfigurationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author watergun
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductConfigurationsServiceImpl extends ServiceImpl<ProductConfigurationsMapper, ProductConfigurations> implements ProductConfigurationsService {

    private final RabbitMqHelper rabbitMqHelper;

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

    /**
     * 商品库存的扣减方法
     * @param configIdAndQuantityMap 配置扣减Map (key: configId, value: 扣减库存)
     * @author watergun
     */
    //todo 加锁
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductConfigurationsQuantity(Map<Long, Integer> configIdAndQuantityMap) {
        log.info("【库存更新】开始: {}", configIdAndQuantityMap);

        if (configIdAndQuantityMap == null || configIdAndQuantityMap.isEmpty()) {
            log.error("【库存更新】参数为空");
            throw new IllegalArgumentException("库存更新参数不能为空");
        }

        // 获取配置ID列表
        List<Long> configIdsList = new ArrayList<>(configIdAndQuantityMap.keySet());

        // 查询库存
        List<ProductConfigurations> productConfigurationsList = this.listByIds(configIdsList);
        if (productConfigurationsList == null || productConfigurationsList.isEmpty()) {
            log.error("【库存更新】查询失败，未找到商品配置: {}", configIdsList);
            throw new RuntimeException("库存查询失败，商品配置不存在");
        }

        // 遍历并更新库存
        List<ProductConfigurations> updatedList = new ArrayList<>();

        for (ProductConfigurations productConfigurations : productConfigurationsList) {
            Long configId = productConfigurations.getConfigId();
            Integer stockChange = configIdAndQuantityMap.get(configId);

            if (stockChange == null) {
                log.warn("【库存更新】configId={} 的库存变更值为空，跳过", configId);
                continue;
            }

            //判断是增加库存还是减少库存
            if (stockChange < 0){
                // 校验库存是否足够
                if (productConfigurations.getStock() < stockChange) {
                    log.error("【库存扣减失败】库存不足: configId={}, 当前库存={}, 需要扣减={}",
                            configId, productConfigurations.getStock(), stockChange);
                    throw new RuntimeException("库存不足，扣减失败");
                }
            }

            // 更新库存
            // Map中存储的value已经处理好，减少存储负值，增加存储正值。
            productConfigurations.setStock(productConfigurations.getStock() + stockChange);
            updatedList.add(productConfigurations);
        }

        // 批量更新数据库
        if (!updatedList.isEmpty()) {
            boolean updateBatchById = this.updateBatchById(updatedList);
            if (!updateBatchById) {
                log.error("【库存扣减】数据库更新失败");
                throw new RuntimeException("库存更新失败");
            }
        }
        // 发送消息到库存日志队列

        log.info("【库存变化】成功: {}", updatedList);
        //定义库存变化日志列表
        // 定义库存变化日志列表
        log.info("【库存扣减】发送消息到库存日志队列");
        List<StockChangeLogDTO> stockChangeLogDTOS = productConfigurationsList.stream()
                .map(productConfigurations -> {
                    Long configId = productConfigurations.getConfigId();
                    Integer delta = configIdAndQuantityMap.getOrDefault(configId, 0); // 避免 NullPointerException

                    return new StockChangeLogDTO(
                            productConfigurations.getProductId(),
                            configId,
                            delta,
                            StockSourceType.SALED
                    );
                })
                .collect(Collectors.toList());
        log.info("原始消息为stockChangeLogDTOS{}",stockChangeLogDTOS);
        String stockMsg = JSONUtil.toJsonStr(stockChangeLogDTOS);
        rabbitMqHelper.sendMessage(StockChangeLogMQConstant.STOCK_LOG_EXCHANGE,
                StockChangeLogMQConstant.STOCK_LOG_ADD_ROUTING_KEY,stockMsg);
        log.info("【库存扣减】发送消息到库存日志队列: {}", stockMsg);

    }
}
