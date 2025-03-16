package com.peacemall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.product.domain.po.Categories;

import java.util.List;
import java.util.Map;

/**
 * @author watergun
 */
public interface CategoriesService extends IService<Categories> {

    //用户查看分类
    //类别按parentId分组
    R<Map<String, List<Categories>>> getCategoriesByParentId();

    //管理员创建分类
    R<String> createCategories(Categories categories);
    //管理员删除分类
    R<String> deleteCategories(Long id);
    //管理员修改分类
    R<String> updateCategories(Categories categories);

    //管理员分页查看所有分类
    R<PageDTO<Categories>> getCategoriesByPage(int page, int pageSize);



}
