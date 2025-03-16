package com.peacemall.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.PageDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.product.domain.po.Categories;
import com.peacemall.product.mapper.CategoriesMapper;
import com.peacemall.product.service.CategoriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author watergun
 */
@Service
@Slf4j
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {


    //用户查看分类
    //类别按parentId分组
    @Override
    public R<Map<String, List<Categories>>> getCategoriesByParentId() {
        log.info("getCategoriesByParentId");
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户信息正常");

        List<Categories> categoriesList = this.list();

        // 按 parentId 进行分组
        Map<String, List<Categories>> categoryMap = categoriesList.stream()
                .collect(Collectors.groupingBy(category ->
                        category.getParentId() == null ? "parent" : category.getParentId().toString()));

        return R.ok(categoryMap);
    }


    //管理员创建分类
    //最多为3级分类
    @Override
    public R<String> createCategories(Categories categories) {
        log.info("createCategories: {}", categories);

        if (categories == null || categories.getCategoryName() == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }

        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户不是管理员");
        }
        log.info("用户信息正常");

        Long parentId = categories.getParentId();
        if (parentId != null) {
            // 获取父分类信息
            Categories parentCategory = this.getById(parentId);
            if (parentCategory == null) {
                return R.error("父分类不存在");
            }

            // 检查父分类的层级是否已经是第 3 级
            if (parentCategory.getParentId() != null) {
                Categories grandParentCategory = this.getById(parentCategory.getParentId());
                if (grandParentCategory != null && grandParentCategory.getParentId() != null) {
                    return R.error("不允许超过三级分类");
                }
            }
        }

        boolean saved = this.save(categories);
        return saved ? R.ok("分类创建成功") : R.error("分类创建失败");
    }


    //管理员删除分类
    //包括所有子分类
    //使用到广度优先算法，来寻找分类以及对应的子分类
    @Override
    @Transactional
    public R<String> deleteCategories(Long id) {
        log.info("deleteCategories: id={}", id);
        if (id == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或无权限");
            return R.error("用户未登录或无权限");
        }
        log.info("用户信息正常，开始查找所有子分类");
        // 一次性查询所有分类，避免递归查询数据库
        List<Categories> allCategories = this.list();
        // 找到所有子分类（不包括自身）
        List<Long> categoryIdsToDelete = findAllChildCategories(id, allCategories);

        // 倒置列表，使深层次的分类(孙分类)先被删除
        Collections.reverse(categoryIdsToDelete);
        // 添加自身 ID
        categoryIdsToDelete.add(id);

        log.info("需要删除的分类ID(从深到浅): {}", categoryIdsToDelete);

        // 按照从深到浅的顺序逐个删除
        for (Long categoryId : categoryIdsToDelete) {
            boolean removed = this.removeById(categoryId);
            if (!removed) {
                log.error("分类删除失败，ID: {}", categoryId);
                return R.error("分类删除失败");
            }
        }

        return R.ok("分类删除成功");
    }

    /**
     * 查找所有子分类（非递归）
     */
    private List<Long> findAllChildCategories(Long parentId, List<Categories> allCategories) {
        List<Long> result = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(parentId);
        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            for (Categories category : allCategories) {
                if (currentId.equals(category.getParentId())) {
                    result.add(category.getCategoryId());
                    queue.add(category.getCategoryId()); // 继续查找子分类
                }
            }
        }
        return result;
    }



    //管理员修改分类
    @Override
    public R<String> updateCategories(Categories category) {
        log.info("updateCategories: {}", category);

        if (category == null || category.getCategoryId() == null) {
            log.error("参数错误");
            return R.error("参数错误");
        }

        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户不是管理员");
        }
        log.info("用户信息正常");

        Long parentId = category.getParentId();
        if (parentId != null) {
            // 获取新的父分类信息
            Categories parentCategory = this.getById(parentId);
            if (parentCategory == null) {
                return R.error("父分类不存在");
            }

            // 检查是否导致层级超过 3 级
            if (parentCategory.getParentId() != null) {
                Categories grandParentCategory = this.getById(parentCategory.getParentId());
                if (grandParentCategory != null && grandParentCategory.getParentId() != null) {
                    return R.error("不允许超过三级分类");
                }
            }
        }

        boolean updated = this.updateById(category);
        return updated ? R.ok("分类更新成功") : R.error("分类更新失败");
    }




    //管理员分页查看所有分类
    @Override
    public R<PageDTO<Categories>> getCategoriesByPage(int page, int pageSize) {
        log.info("getCategoriesByPage,page:{},pageSize:{}", page, pageSize);
        if (page <= 0 || pageSize <= 0) {
            log.error("参数错误");
            return R.error("参数错误");
        }
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userId == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户不是管理员");

        }
        log.info("用户信息正常");
        Page<Categories> categoriesPage = this.page(new Page<>(page, pageSize));
        return R.ok(PageDTO.of(categoriesPage));
    }


}
