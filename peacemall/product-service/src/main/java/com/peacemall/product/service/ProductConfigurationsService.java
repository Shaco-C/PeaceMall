package com.peacemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.IdsDTO;
import com.peacemall.product.domain.dto.ProductConfigDTO;
import com.peacemall.product.domain.po.ProductConfigurations;

import java.util.List;

/**
 * @author watergun
 */
public interface ProductConfigurationsService extends IService<ProductConfigurations> {

    //创建商品配置
    R<String> merchantCreateProductConfigurations(ProductConfigDTO productConfigDTO);

    //删除商品配置
    R<String> merchantDeleteProductConfigurations(List<Long> configurationsList);

    //修改商品配置
    R<String> merchantUpdateProductConfigurations(ProductConfigurations productConfigurations);

    //根据商品id删除所有商品配置
    void merchantDeleteProductConfigurationsByProductId(Long productId);

    //根据商品id查询商品配置
    List<ProductConfigurations> queryProductConfigurationsByProductId(Long productId);

    //todo 商品配置数量的增减（购买，退货，补货）

}
