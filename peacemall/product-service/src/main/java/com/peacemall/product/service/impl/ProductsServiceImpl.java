package com.peacemall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.ShopClient;
import com.peacemall.common.constant.EsOperataionMQConstant;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductConfigurationDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.domain.dto.ProductDetailsDTO;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.RabbitMqHelper;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.dto.AddProductDTO;

import com.peacemall.product.domain.po.Categories;
import com.peacemall.product.domain.po.ProductConfigurations;
import com.peacemall.product.domain.po.ProductImages;
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
import java.util.function.Function;
import java.util.stream.Collectors;


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
    private final ShopClient shopClient;
    private final RabbitMqHelper rabbitMqHelper;

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
        log.info("用户信息: userId:{},userRole:{}", userId, userRole);

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
        ProductDTO productDTO = baseMapper.findProductDTOById(productId);
        String message = JSONUtil.toJsonStr(productDTO);
        try{
            log.info("保存商品信息到ES中");
            rabbitMqHelper.sendMessage(EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME,
                    EsOperataionMQConstant.ES_ADD_PRODUCT_ROUTING_KEY,message);
        }catch (Exception e){
            log.error("保存商品信息到ES中失败");
        }
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
        List<Long> list = List.of(productId);
        String listMessage = JSONUtil.toJsonStr(list);
        try{
            log.info("删除商品信息到ES中");
            rabbitMqHelper.sendMessage(EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME,
                    EsOperataionMQConstant.ES_DELETE_PRODUCT_ROUTING_KEY,listMessage);
        }catch (Exception e){
            log.error("删除商品信息到ES中失败");
        }


        return R.ok("商品删除成功");
    }

    @Override
    public R<PageDTO<ProductDTO>> getProductsByCategoryId(int page, int pageSize, Long categoryId) {
        log.info("查询分类下的商品 - page: {}, pageSize: {}, categoryId: {}", page, pageSize, categoryId);

        // 参数校验
        if (page <= 0 || pageSize <= 0 || categoryId == null) {
            log.error("查询分类商品失败 - 参数错误");
            return R.error("参数错误");
        }

        // 获取当前用户 ID（确保用户已登录）
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("查询分类商品失败 - 用户未登录");
            return R.error("用户未登录");
        }

        log.debug("用户 ID: {}", userId);

        // 获取所有分类信息，并递归查找当前分类的所有子分类 ID
        List<Categories> allCategories = categoriesService.list();
        List<Long> categoryIds = new ArrayList<>(findAllChildCategoriesOptimized(categoryId, allCategories));
        categoryIds.add(categoryId); // 添加当前分类 ID

        log.debug("当前分类及其子分类 IDs: {}", categoryIds);

        // 构建查询条件，查询符合分类 ID 的商品
        LambdaQueryWrapper<Products> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Products::getCategoryId, categoryIds);

        // 分页查询商品数据
        Page<Products> productsPage = this.page(new Page<>(page, pageSize), queryWrapper);

        // 转换实体类为 DTO
        List<ProductDTO> productDTOs = BeanUtil.copyToList(productsPage.getRecords(), ProductDTO.class);

        // 批量查询商品主图
        if (!productDTOs.isEmpty()) {
            List<Long> productIds = productDTOs.stream().map(ProductDTO::getProductId).collect(Collectors.toList());
            Map<Long, String> mainImageUrls = productImagesService.getMainImageUrlsByProductIds(productIds);

            // 赋值图片 URL
            productDTOs.forEach(product -> product.setImageUrl(mainImageUrls.get(product.getProductId())));
        }

        // 组装分页返回对象
        log.info("查询分类商品成功 - 查询到 {} 条数据", productDTOs.size());
        return R.ok(PageDTO.of(productsPage,productDTOs));
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
        ProductDTO productDTO = BeanUtil.copyProperties(products,ProductDTO.class);
        String message = JSONUtil.toJsonStr(productDTO);
        try{
            log.info("更新商品信息到ES中");
            rabbitMqHelper.sendMessage(EsOperataionMQConstant.ES_OPERATION_PRODUCT_EXCHANGE_NAME,
                    EsOperataionMQConstant.ES_UPDATE_PRODUCT_ROUTING_KEY,message);
        }catch (Exception e){
            log.error("更新商品信息到ES中失败");
        }
        return R.ok("商品更新成功");
    }

    //商家查看自己商品的基本信息
    @Override
    public R<PageDTO<Products>> merchantGetProductInfo(int page, int pageSize, ProductStatus productStatus) {
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
        return R.ok(PageDTO.of(productsPage));
    }

    //通过productId查询商品的基本信息
    //在favorites中被调用
    //通过feign接口调用
    @Override
    public Map<Long, ProductBasicInfosAndShopInfos> getProductBasicInfosAndShopInfosById(List<Long> productIds) {
        log.info("getProductBasicInfosAndShopInfosById called, productIds: {}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            log.error("productIds 为空");
            throw new IllegalArgumentException("参数错误: productIds 不能为空");
        }

        // 批量查询商品信息
        List<Products> productsList = this.listByIds(productIds);
        if (productsList == null || productsList.isEmpty()) {
            log.error("查询不到商品信息");
            throw new RuntimeException("商品不存在");
        }

        // 获取所有商家 ID（去重，防止重复查询）
        List<Long> shopIdList = productsList.stream()
                .map(Products::getShopId)
                .distinct()
                .collect(Collectors.toList());

        // 通过 Feign 调用商家信息
        Map<Long, ShopsInfoVO> shopInfoMap = shopClient.getShopInfoByIds(shopIdList);

        // 组装商品和商家信息，并转换为 Map<productId, ProductBasicInfosAndShopInfos>
        Map<Long, ProductBasicInfosAndShopInfos> productInfoMap = productsList.stream()
                .collect(Collectors.toMap(
                        Products::getProductId,
                        product -> {
                            ProductBasicInfosAndShopInfos vo = new ProductBasicInfosAndShopInfos();
                            BeanUtil.copyProperties(product, vo);

                            // 获取商家信息并填充
                            ShopsInfoVO shopInfo = shopInfoMap.get(product.getShopId());
                            if (shopInfo != null) {
                                vo.setShopName(shopInfo.getShopName());
                                vo.setShopDescription(shopInfo.getShopDescription());
                                vo.setShopAvatarUrl(shopInfo.getShopAvatarUrl());
                            } else {
                                log.warn("商家 ID {} 对应的商家信息不存在", product.getShopId());
                            }

                            return vo;
                        }
                ));

        log.info("getProductBasicInfosAndShopInfosById success, result size: {}", productInfoMap.size());
        return productInfoMap;
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

        log.info("shopId:{}",products.getShopId());

        //通过openfeign来查询商品的商家信息
        try {
            ShopsInfoVO shopsInfoVO = shopClient.getShopInfoById(products.getShopId());
            if (shopsInfoVO != null) {
                productDetailsVO.setShopName(shopsInfoVO.getShopName());
                productDetailsVO.setShopDescription(shopsInfoVO.getShopDescription());
                productDetailsVO.setShopAvatarUrl(shopsInfoVO.getShopAvatarUrl());
            } else {
                log.warn("无法获取店铺信息，shopId: {}", products.getShopId());
            }
        } catch (Exception e) {
            log.error("获取店铺信息时出错，shopId: {}", products.getShopId(), e);
            // 这里可以设置默认值或者继续返回，取决于你的业务需求
            productDetailsVO.setShopName("未知店铺");
        }

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
    public R<PageDTO<Products>> adminGetProductsToAudit(int page, int pageSize, ProductStatus productStatus) {
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
        return R.ok(PageDTO.of(productsPage));
    }

    @Override
    public PageDTO<ProductDTO> findAllProductsWithPage(int page, int size) {
        // 创建分页对象
        Page<ProductDTO> pageResult = new Page<>(page, size);

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询总记录数
        int total = baseMapper.countProducts();

        // 查询当前页数据
        List<ProductDTO> pageRecords = baseMapper.findProductsWithPage(offset, size);

        // 设置分页结果
        pageResult.setRecords(pageRecords);
        pageResult.setTotal(total);

        return PageDTO.of(pageResult);
    }

    @Override
    public Map<Long, ProductDetailsDTO> getProductDetailsByIds(List<Long> productIds, List<Long> configIds) {
        log.info("getProductDetailsByIds, productIds:{}", productIds);
        if (productIds == null || productIds.isEmpty()) {
            log.error("参数错误");
            return Collections.emptyMap();
        }

        // 1️⃣ 查询商品基本信息
        List<Products> productsList = this.listByIds(productIds);
        if (productsList.isEmpty()) {
            log.warn("未查询到商品信息");
            return Collections.emptyMap();
        }

        // 2️⃣ 查询商品配置信息
        List<ProductConfigurations> productConfigurationsList = productConfigurationsService.listByIds(configIds);
        List<ProductConfigurationDTO> productConfigurationDTOList = BeanUtil.copyToList(productConfigurationsList, ProductConfigurationDTO.class);
        Map<Long, List<ProductConfigurationDTO>> productConfigurationsMap = productConfigurationDTOList.stream()
                .collect(Collectors.groupingBy(ProductConfigurationDTO::getProductId));

        // 3️⃣ 查询商品图片信息
        List<ProductImages> productImagesList = productImagesService.list(
                new LambdaQueryWrapper<ProductImages>().in(ProductImages::getProductId, productIds)
        );
        Map<Long, String> productImageMap = productImagesList.stream()
                .collect(Collectors.groupingBy(ProductImages::getProductId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<ProductImages> images = entry.getValue();
                            return images.stream()
                                    .filter(ProductImages::getIsMain)
                                    .map(ProductImages::getUrl)
                                    .findFirst()
                                    .orElseGet(() -> images.stream()
                                            .max(Comparator.comparingInt(ProductImages::getSortOrder))
                                            .map(ProductImages::getUrl)
                                            .orElse(null));
                        }
                ));

        // 4️⃣ 查询商家信息（远程调用）
        List<Long> shopIds = productsList.stream().map(Products::getShopId).distinct().collect(Collectors.toList());
        Map<Long, ShopsInfoVO> shopInfoByIds = shopIds.isEmpty() ? Collections.emptyMap() : shopClient.getShopInfoByIds(shopIds);

        // 5️⃣ 组装信息
        List<ProductDetailsDTO> productDetailsDTOList = BeanUtil.copyToList(productsList, ProductDetailsDTO.class);
        for (ProductDetailsDTO productDetailsDTO : productDetailsDTOList) {
            // 设置商品配置信息
            productDetailsDTO.setConfigurations(productConfigurationsMap.getOrDefault(productDetailsDTO.getProductId(), Collections.emptyList()));

            // 设置商品图片
            productDetailsDTO.setUrl(productImageMap.getOrDefault(productDetailsDTO.getProductId(), "默认图片URL"));

            // 设置商家名称
            ShopsInfoVO shopInfo = shopInfoByIds.get(productDetailsDTO.getShopId());
            productDetailsDTO.setShopName(shopInfo != null ? shopInfo.getShopName() : "未知商家");
        }

        // 6️⃣ 转换为 Map
        return productDetailsDTOList.stream()
                .collect(Collectors.toMap(ProductDetailsDTO::getProductId, Function.identity(), (existing, replacement) -> existing));
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
