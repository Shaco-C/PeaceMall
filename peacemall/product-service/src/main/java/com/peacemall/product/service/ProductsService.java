package com.peacemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.domain.dto.ProductDTO;
import com.peacemall.common.domain.dto.ProductDetailsDTO;
import com.peacemall.common.domain.vo.ProductBasicInfosAndShopInfos;
import com.peacemall.product.domain.dto.AddProductDTO;
import com.peacemall.product.domain.po.Products;
import com.peacemall.product.domain.vo.ProductDetailsVO;
import com.peacemall.product.enums.ProductStatus;

import java.util.List;
import java.util.Map;

/**
 * @author watergun
 */
public interface ProductsService extends IService<Products> {

    //创建商品
    R<Long> merchantCreateProduct(AddProductDTO addProductDTO);

    //商家修改商品上下架状态
    R<String> merchantUpdateProductActiveStatus(Long productId,Boolean activeStatus);

    //商家删除商品
    R<String> merchantDeleteProduct(Long productId);

    //分页查询查看某个分类下的产品
    R<PageDTO<ProductDTO>> getProductsByCategoryId(int page, int pageSize, Long categoryId);

    //修改商品基本信息
    R<String> merchantUpdateProduct(Products products);

    //商家查看自己商品的基本信息
    R<PageDTO<Products>> merchantGetProductInfo(int page, int pageSize, ProductStatus productStatus);

    //根据商品id查询基本信息
    Map<Long, ProductBasicInfosAndShopInfos> getProductBasicInfosAndShopInfosById(List<Long> productIds);

    //根据id查看详细信息，以及其配置
    R<ProductDetailsVO> getProductDetailsById(Long productId);

    //管理员审核商品
    R<String> adminAuditProduct(Long productId, ProductStatus productStatus);

    //管理员查看待审核商品信息
    R<PageDTO<Products>> adminGetProductsToAudit(int page, int pageSize, ProductStatus productStatus);

    //查看所有的商品信息
    //用于将信息保存到es中
    PageDTO<ProductDTO> findAllProductsWithPage(int page, int size);


    //批量获取商品的信息
    Map<Long, ProductDetailsDTO> getProductDetailsByIds(List<Long> productIds,List<Long> configIds);

    //通过shopId分页查询商品信息
    PageDTO<ProductDTO> getProductByShopId(int page, int size, Long shopId);

}
