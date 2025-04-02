package com.peacemall.cartItem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

    private final ProductClient productClient;


    @Override
    public R<String> addProductToCart(CartItemDTO cartItemDTO) {
        log.info("【添加商品到购物车】请求参数: {}", cartItemDTO);

        // 1. 参数校验
        if (Objects.isNull(cartItemDTO) || Objects.isNull(cartItemDTO.getConfigId()) || cartItemDTO.getQuantity() <= 0) {
            log.error("【添加商品到购物车】参数非法: {}", cartItemDTO);
            return R.error("商品信息为空或数量非法");
        }

        Long userId = UserContext.getUserId();
        if (Objects.isNull(userId)) {
            log.error("【添加商品到购物车】用户未登录");
            return R.error("用户未登录");
        }

        // 2. 查询购物车是否已有该商品
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getConfigId, cartItemDTO.getConfigId());

        CartItem existCartItem = this.getOne(queryWrapper);

        boolean success;
        if (existCartItem != null) {
            // 3.1 购物车已存在该商品，更新数量
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItemDTO.getQuantity());
            success = this.updateById(existCartItem);
            log.info("【添加商品到购物车】更新购物车: {}, 更新结果: {}", existCartItem, success);
        } else {
            // 3.2 购物车不存在该商品，新增
            CartItem cartItem = BeanUtil.copyProperties(cartItemDTO, CartItem.class);
            cartItem.setUserId(userId);
            success = this.save(cartItem);
            log.info("【添加商品到购物车】新增购物车: {}, 添加结果: {}", cartItem, success);
        }

        if (!success) {
            log.error("【添加商品到购物车】操作失败: userId={}, cartItemDTO={}", userId, cartItemDTO);
            return R.error("添加商品到购物车失败");
        }

        return R.ok("添加商品到购物车成功");
    }


    @Override
    public R<String> deleteProductFromCartBatch(List<Long> cartItemIds) {
        log.info("deleteProductFromCartBatch: {}", cartItemIds);
        if (CollectionUtils.isEmpty(cartItemIds)) {
            log.error("deleteProductFromCartBatch: cartItemIds is empty");
            return R.error("商品信息为空");
        }
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId).in(CartItem::getCartItemId, cartItemIds);
        boolean remove = this.remove(queryWrapper);
        if (!remove) {
            log.error("deleteProductFromCartBatch: remove cartItem failed");
            return R.error("删除商品失败");
        }
        return R.ok("删除商品成功");
    }

    @Override
    public R<String> updateProductQuantity(List<CartItemDTO> cartItemDTOList) {
        if (cartItemDTOList == null || cartItemDTOList.isEmpty()) {
            log.error("updateProductQuantity: 购物车商品列表为空");
            return R.error("参数错误");
        }

        Long userId = UserContext.getUserId();
        Map<String, CartItem> cartItemMap = new HashMap<>(); // Key: productId_configId
        List<CartItem> updatedCartItems = new ArrayList<>();

        // 1. 批量查询所有购物车商品，减少 SQL 查询次数
        List<Long> productIds = cartItemDTOList.stream()
                .map(CartItemDTO::getProductId)
                .filter(Objects::nonNull) // 过滤 null 值，避免 SQL 报错
                .collect(Collectors.toList());

        List<Long> configIds = cartItemDTOList.stream()
                .map(CartItemDTO::getConfigId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<CartItem> cartItems = this.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(!productIds.isEmpty(), CartItem::getProductId, productIds)
                .and(!configIds.isEmpty(), q -> q.in(CartItem::getConfigId, configIds).or().isNull(CartItem::getConfigId))
        );

        // 存入 Map，Key 采用 "productId_configId"
        for (CartItem cartItem : cartItems) {
            String key = cartItem.getProductId() + "_" + (cartItem.getConfigId() == null ? "null" : cartItem.getConfigId());
            cartItemMap.put(key, cartItem);
        }

        // 2. 遍历 DTO，查找对应的 CartItem 并更新数量
        for (CartItemDTO dto : cartItemDTOList) {
            if (dto.getProductId() == null || dto.getQuantity() == null || dto.getQuantity() <= 0) {
                log.warn("updateProductQuantity: 跳过参数错误的数据，productId={}, quantity={}", dto.getProductId(), dto.getQuantity());
                continue; // 跳过错误数据，而不是返回错误
            }

            String key = dto.getProductId() + "_" + (dto.getConfigId() == null ? "null" : dto.getConfigId());
            CartItem cartItem = cartItemMap.get(key);

            if (cartItem == null) {
                log.warn("updateProductQuantity: 购物车商品不存在，跳过 productId={}, configId={}", dto.getProductId(), dto.getConfigId());
                continue; // 跳过不存在的商品
            }

            cartItem.setQuantity(dto.getQuantity());
            updatedCartItems.add(cartItem);
        }

        // 3. 批量更新数据库
        if (!updatedCartItems.isEmpty()) {
            boolean updated = this.updateBatchById(updatedCartItems);
            if (!updated) {
                log.error("updateProductQuantity: 批量更新购物车商品失败");
                return R.error("更新商品数量失败");
            }
            log.info("updateProductQuantity: 成功更新 {} 个商品", updatedCartItems.size());
        } else {
            log.warn("updateProductQuantity: 没有任何商品被更新");
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
