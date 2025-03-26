package com.peacemall.cartItem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.ProductClient;
import com.peacemall.cartItem.domain.dto.CartItemDTO;
import com.peacemall.cartItem.domain.po.CartItem;
import com.peacemall.cartItem.domain.vo.CartItemVO;
import com.peacemall.cartItem.mapper.CartItemMapper;
import com.peacemall.cartItem.service.CartItemService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductConfigurationDTO;
import com.peacemall.common.domain.dto.ProductDetailsDTO;
import com.peacemall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

    private final ProductClient productClient;


    @Override
    public R<String> addProductToCart(CartItemDTO cartItemDTO) {
        log.info("addProductToCart: {}", cartItemDTO);
        if (cartItemDTO == null) {
            log.error("addProductToCart: cartItemDTO is null");
            return R.error("商品信息为空");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("addProductToCart: userId is null");
            return R.error("用户未登录");
        }
        CartItem cartItem = BeanUtil.copyProperties(cartItemDTO, CartItem.class);
        cartItem.setUserId(userId);
        boolean save = this.save(cartItem);
        if (!save) {
            log.error("addProductToCart: save cartItem failed");
            return R.error("添加商品到购物车失败");
        }
        return R.ok("添加商品到购物车成功");
    }

    @Override
    public R<String> deleteProductFromCart(Long cartItemId) {
        log.info("deleteProductFromCart: {}", cartItemId);
        if (cartItemId == null) {
            log.error("deleteProductFromCart: cartItemId is null");
            return R.error("商品信息为空");
        }
        CartItem cartItem = this.getById(cartItemId);
        if (cartItem == null) {
            log.error("deleteProductFromCart: cartItem is null");
            return R.error("商品不存在，请刷新重试");
        }
        Long userId = UserContext.getUserId();
        if (!cartItem.getUserId().equals(userId)) {
            log.error("deleteProductFromCart: userId is not match");
            return R.error("用户信息不匹配");
        }
        boolean remove = this.removeById(cartItemId);
        if (!remove) {
            log.error("deleteProductFromCart: remove cartItem failed");
            return R.error("删除商品失败");
        }
        return R.ok("删除商品成功");
    }

    @Override
    public R<String> updateProductQuantity(Long cartItemId, Integer quantity) {
        log.info("updateProductQuantity: {}, {}", cartItemId, quantity);
        if (cartItemId == null || quantity == null ||quantity<=0) {
            log.error("updateProductQuantity: cartItemId or quantity is null");
            return R.error("参数错误");
        }
        CartItem cartItem = this.getById(cartItemId);
        if (cartItem == null) {
            log.error("updateProductQuantity: cartItem is null");
            return R.error("商品不存在，请刷新重试");
        }
        Long userId = UserContext.getUserId();
        if (!cartItem.getUserId().equals(userId)) {
            log.error("updateProductQuantity: userId is not match");
            return R.error("用户信息不匹配");
        }
        cartItem.setQuantity(quantity);
        boolean update = this.updateById(cartItem);
        if (!update) {
            log.error("updateProductQuantity: update cartItem failed");
            return R.error("更新商品数量失败");
        }
        return R.ok("更新商品数量成功");
    }

    @Override
    public R<PageDTO<CartItemVO>> showCartItems(int page, int pageSize) {
        log.info("showCartItems: page={}, pageSize={}", page, pageSize);

        // 校验分页参数
        if (page <= 0 || pageSize <= 0) {
            log.error("showCartItems: page or pageSize is invalid");
            return R.error("参数错误");
        }

        // 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("showCartItems: userId is null");
            return R.error("用户未登录");
        }

        // 查询购物车记录
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        Page<CartItem> pageParam = new Page<>(page, pageSize);
        this.page(pageParam, queryWrapper);

        List<CartItemVO> cartItemVOS = BeanUtil.copyToList(pageParam.getRecords(), CartItemVO.class);
        if (cartItemVOS.isEmpty()) {
            log.info("showCartItems: 购物车为空");
            return R.ok(PageDTO.empty(0L,0L));
        }

        // 获取商品 ID 和配置 ID
        List<Long> productIds = cartItemVOS.stream().map(CartItemVO::getProductId).distinct().collect(Collectors.toList());
        List<Long> productConfigIds = cartItemVOS.stream().map(CartItemVO::getConfigId).distinct().collect(Collectors.toList());

        // 查询商品详情信息
        Map<Long, ProductDetailsDTO> detailsByIds = productClient.getProductDetailsByIds(productIds, productConfigIds);
        if (detailsByIds == null || detailsByIds.isEmpty()) {
            log.error("showCartItems: 商品详情查询失败, productIds={}, productConfigIds={}", productIds, productConfigIds);
            return R.error("获取商品详情失败");
        }

        // 生成商品配置 Map（以 configId 为 key）
        Map<Long, ProductConfigurationDTO> productConfigurationDTOMap = detailsByIds.values().stream()
                .flatMap(dto -> dto.getConfigurations().stream())
                .collect(Collectors.toMap(
                        ProductConfigurationDTO::getConfigId,
                        Function.identity(),
                        (existing, replacement) -> existing // 如果有重复 configId，保留已有值
                ));

        // 组装购物车信息
        for (CartItemVO cartItemVO : cartItemVOS) {
            Long configId = cartItemVO.getConfigId();

            // 获取商品详情
            ProductDetailsDTO productDetailsDTO = detailsByIds.get(cartItemVO.getProductId());
            if (productDetailsDTO == null) {
                log.warn("showCartItems: 未找到商品信息, productId={}", cartItemVO.getProductId());
                continue;
            }

            // 设置商品基本信息
            cartItemVO.setProductName(productDetailsDTO.getName());
            cartItemVO.setBrand(productDetailsDTO.getBrand());
            cartItemVO.setUrl(productDetailsDTO.getUrl());
            cartItemVO.setShopId(productDetailsDTO.getShopId());
            cartItemVO.setShopName(productDetailsDTO.getShopName());

            // 获取商品配置信息
            ProductConfigurationDTO configDTO = productConfigurationDTOMap.get(configId);
            if (configDTO != null) {
                cartItemVO.setConfiguration(configDTO.getConfiguration());
                cartItemVO.setPrice(configDTO.getPrice());
            } else {
                log.warn("showCartItems: 未找到商品配置, configId={}", configId);
            }
        }

        // 组装分页数据
        PageDTO<CartItemVO> pageDTO = new PageDTO<>();
        pageDTO.setList(cartItemVOS);
        pageDTO.setTotal(pageParam.getTotal());
        pageDTO.setPages(pageParam.getPages());
        return R.ok(pageDTO);
    }

    @Override
    public boolean deleteCartItemByConfigIds(List<Long> configIds, Long userId) {
        log.info("deleteCartItemByConfigIds: configIds={}, userId={}", configIds, userId);

        if (Objects.isNull(userId) || CollectionUtils.isEmpty(configIds)) {
            log.error("deleteCartItemByConfigIds: 参数错误，configIds={}，userId={}", configIds, userId);
            throw new IllegalArgumentException("参数错误：configIds 不能为空，userId 不能为空");
        }

        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CartItem::getConfigId, configIds)
                .eq(CartItem::getUserId, userId);

        boolean removed = this.remove(queryWrapper);
        if (!removed) {
            log.warn("deleteCartItemByConfigIds: 删除购物车商品失败, configIds={}, userId={}", configIds, userId);
            throw new RuntimeException("删除购物车商品失败");
        }

        log.info("删除购物车商品成功, configIds={}, userId={}", configIds, userId);
        return true;
    }


}
