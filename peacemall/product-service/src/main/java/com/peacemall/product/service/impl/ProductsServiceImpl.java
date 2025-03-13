package com.peacemall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.dto.AddProductDTO;

import com.peacemall.product.domain.po.Categories;
import com.peacemall.product.domain.po.Products;
import com.peacemall.product.domain.vo.ProductDetailsVO;
import com.peacemall.product.enums.ProductStatus;
import com.peacemall.product.mapper.ProductsMapper;
import com.peacemall.product.service.CategoriesService;
import com.peacemall.product.service.ProductConfigurationsService;
import com.peacemall.product.service.ProductImagesService;
import com.peacemall.product.service.ProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * @author watergun
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {

    private final ProductImagesService productImagesService;
    private final ProductConfigurationsService productConfigurationsService;
    private final CategoriesService categoriesService;

    //创建商品
    @Override
    @Transactional
    public R<Long> merchantCreateProduct(AddProductDTO addProductDTO) {
        log.info("merchantCreateProduct: {}", addProductDTO);

        if (addProductDTO == null) {
            log.error("商品信息为空");
            return R.error("商品信息不能为空");
        }

        log.info("商品信息正常");

        Long userId = UserContext.getUserId();
        String userRole =UserContext.getUserRole();

        //校验用户权限
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)){
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");

        //信息验证完毕，准备开始保存商品信息
        Products products = new Products();
        BeanUtil.copyProperties(addProductDTO,products);
        log.info("商品信息: {}", products);
        products.setUserId(userId);
        products.setSales(0);
        products.setStatus(ProductStatus.PENDING);
        products.setIsActive(true);

        boolean save = this.save(products);
        if (!save) {
            log.error("商品信息保存失败");
            return R.error("商品信息保存失败");
        }
        log.info("商品信息保存成功");

        Long productId = products.getProductId();

        //开始执行保存商品图片逻辑
        addProductDTO.getProductImages().forEach(productImages -> productImages.setProductId(productId));
        boolean saveProductImages = productImagesService.saveProductImages(addProductDTO.getProductImages());
        if (!saveProductImages) {
            log.error("商品图片保存失败");
            throw new RuntimeException("商品图片保存失败");
        }
        log.info("商品图片保存成功");
        return R.ok(productId);
    }

    //商家修改商品上下架状态
    @Override
    public R<String> merchantUpdateProductActiveStatus(Long productId, Boolean activeStatus) {
        log.info("merchantUpdateProductActiveStatus,productId:{},activeStatus:{}", productId, activeStatus);
        if (productId == null || activeStatus == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");
        Products products = this.getById(productId);
        if (products == null) {
            log.error("商品不存在");
            return R.error("商品不存在");
        }

        //验证该商品是否为该用户的
        if (!products.getUserId().equals(userId)){
            log.error("该商品不属于该用户");
            return R.error("该商品不属于该用户");
        }

        products.setIsActive(!activeStatus);
        boolean update = this.updateById(products);
        if (!update) {
            log.error("商品状态更新失败");
            return R.error("商品状态更新失败");
        }
        log.info("商品状态更新成功");
        return R.ok("商品状态更新成功");
    }

    //商家删除商品
    @Override
    @Transactional
    public R<String> merchantDeleteProduct(Long productId) {
        log.info("merchantDeleteProduct,productId:{}", productId);
        if (productId == null) {
            log.error("productId为空");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");

        Products products = this.getById(productId);
        if (products == null) {
            log.error("商品不存在");
            return R.error("商品不存在");
        }
        //验证该商品是否为该用户的
        if (!products.getUserId().equals(userId)){
            log.error("该商品不属于该用户");
            return R.error("该商品不属于该用户");
        }

        //删除该商品的所有配置信息
        productConfigurationsService.merchantDeleteProductConfigurationsByProductId(productId);

        //根据商品Id删除所有图片
        productImagesService.deleteProductImagesByProductId(productId);

        boolean remove = this.removeById(productId);
        if (!remove) {
            log.error("商品删除失败");
            return R.error("商品删除失败");
        }
        log.info("商品删除成功");



        return R.ok("商品删除成功");
    }

    @Override
    public R<Page<Products>> getProductsByCategoryId(int page, int pageSize, Long categoryId) {
        log.info("getProductsByCategoryId,page:{},pageSize:{},categoryId:{}", page, pageSize, categoryId);
        if (page <= 0 || pageSize <= 0 || categoryId == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户信息正常");

        // 查询所有分类信息
        List<Categories> allCategories = categoriesService.list();

        // 获取当前分类及其所有子分类、孙分类的ID列表
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId); // 添加当前分类ID
        categoryIds.addAll(findAllChildCategoriesOptimized(categoryId, allCategories));

        log.info("查询分类及其子分类IDs: {}", categoryIds);

        // 查询这些分类下的所有商品
        LambdaQueryWrapper<Products> productsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productsLambdaQueryWrapper.in(Products::getCategoryId, categoryIds);

        Page<Products> productsPage = this.page(new Page<>(page, pageSize), productsLambdaQueryWrapper);
        return R.ok(productsPage);
    }

    //修改商品基本信息
    @Override
    public R<String> merchantUpdateProduct(Products products) {
        log.info("merchantUpdateProduct,products:{}", products);
        if (products == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");

        //验证该商品是否为该用户的
        if (!products.getUserId().equals(userId)){
            log.error("该商品不属于该用户");
            return R.error("该商品不属于该用户");
        }

        boolean update = this.updateById(products);
        if (!update) {
            log.error("商品更新失败");
            return R.error("商品更新失败");
        }
        log.info("商品更新成功");
        return R.ok("商品更新成功");
    }

    //商家查看自己商品的基本信息
    @Override
    public R<Page<Products>> merchantGetProductInfo(int page, int pageSize, ProductStatus productStatus) {
        log.info("merchantGetProductInfo,page:{},pageSize:{},productStatus:{}", page, pageSize, productStatus);
        if (page <= 0 || pageSize <= 0 || productStatus== null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.MERCHANT.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");
        LambdaQueryWrapper<Products> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Products::getUserId, userId)
                .eq(Products::getStatus, productStatus);
        Page<Products> productsPage = new Page<>(page, pageSize);
        this.page(productsPage, queryWrapper);
        return R.ok(productsPage);
    }

    //根据id查看基本详细信息，以及其配置
    //所有用户通过点击商品的基本信息
    //下一个页面就是商品的详细信息
    @Override
    public R<ProductDetailsVO> getProductDetailsById(Long productId) {
        log.info("getProductDetailsById,productId:{}", productId);
        if (productId == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户信息正常");
        Products products = this.getById(productId);
        if (products == null) {
            log.error("商品不存在");
            return R.error("商品不存在");
        }
        log.info("商品信息正常");
        //将商品信息复制到VO中
        ProductDetailsVO productDetailsVO = new ProductDetailsVO();
        BeanUtil.copyProperties(products, productDetailsVO);
        log.info("商品信息复制成功,productDetailsVO:{}", productDetailsVO);

        //根据商品id查询商品的配置信息

        productDetailsVO.setProductConfigurationsList(
                productConfigurationsService.queryProductConfigurationsByProductId(productId)
        );

        //根据商品id查询商品的图片信息
        productDetailsVO.setProductImagesList(
                productImagesService.getProductImagesByProductId(productId)
        );

        //返回一整个VO
        return R.ok(productDetailsVO);
    }

    @Override
    public R<String> adminAuditProduct(Long productId, ProductStatus productStatus) {
        log.info("adminAuditProduct,productId:{},productStatus:{}", productId, productStatus);
        if (productId == null || productStatus == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");
        Products products = this.getById(productId);
        if (products == null) {
            log.error("商品不存在");
            return R.error("商品不存在");
        }
        if (products.getStatus() != ProductStatus.PENDING) {
            log.error("商品状态不对");
            return R.error("商品已被处理");
        }
        products.setStatus(productStatus);
        this.updateById(products);
        return R.ok("审核成功");
    }

    @Override
    public R<Page<Products>> adminGetProductsToAudit(int page, int pageSize, ProductStatus productStatus) {
        log.info("adminGetProductsToAudit,page:{},pageSize:{},productStatus:{}", page, pageSize, productStatus);
        if (page < 1 || pageSize < 1 || productStatus == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或权限不对");
            return R.error("用户未登录或权限不对");
        }
        log.info("用户信息正常");
        LambdaQueryWrapper<Products> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Products::getStatus, productStatus);
        Page<Products> productsPage = this.page(new Page<>(page, pageSize), queryWrapper);
        return R.ok(productsPage);
    }

    private List<Long> findAllChildCategoriesOptimized(Long parentId, List<Categories> allCategories) {
        List<Long> result = new ArrayList<>();

        // 预处理: 构建父ID到子分类列表的映射
        Map<Long, List<Categories>> parentToChildren = new HashMap<>();
        for (Categories category : allCategories) {
            if (category.getParentId() != null) {
                parentToChildren.computeIfAbsent(category.getParentId(), k -> new ArrayList<>())
                        .add(category);
            }
        }

        // 使用广度优先搜索
        Queue<Long> queue = new LinkedList<>();
        queue.add(parentId);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            List<Categories> children = parentToChildren.get(currentId);

            if (children != null) {
                for (Categories child : children) {
                    result.add(child.getCategoryId());
                    queue.add(child.getCategoryId());
                }
            }
        }

        return result;
    }
}
