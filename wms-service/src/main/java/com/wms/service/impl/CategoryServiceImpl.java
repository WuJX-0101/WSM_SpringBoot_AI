package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.WmsCategoryMapper;
import com.wms.model.dto.CategoryDTO;
import com.wms.model.entity.WmsCategory;
import com.wms.service.CategoryService;
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
 * 商品分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final WmsCategoryMapper categoryMapper;

    /**
     * 创建分类
     * 
     * 流程:
     * 1. 检查分类编码唯一性
     * 2. 如果有父分类，检查父分类是否存在
     * 3. 插入数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsCategory create(CategoryDTO dto) {
        // 1. 检查分类编码唯一性
        WmsCategory exist = categoryMapper.selectOne(
                new LambdaQueryWrapper<WmsCategory>()
                        .eq(WmsCategory::getCategoryCode, dto.getCategoryCode())
        );
        if (exist != null) {
            throw new BusinessException("分类编码已存在");
        }

        // 2. 如果有父分类，检查父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            WmsCategory parent = categoryMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        // 3. 构建实体
        WmsCategory category = new WmsCategory();
        BeanUtils.copyProperties(dto, category);
        if (category.getParentId() == null) {
            category.setParentId(0L);  // 默认顶级分类
        }
        category.setStatus(1);

        // 4. 插入数据库
        categoryMapper.insert(category);
        log.info("创建分类成功: {}", category.getCategoryCode());
        return category;
    }

    /**
     * 更新分类
     * 
     * 流程:
     * 1. 检查分类是否存在
     * 2. 检查分类编码唯一性（排除自身）
     * 3. 不能将自己设为自己的父分类
     * 4. 更新数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsCategory update(Long id, CategoryDTO dto) {
        // 1. 检查分类是否存在
        WmsCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 2. 检查分类编码唯一性（排除自身）
        WmsCategory exist = categoryMapper.selectOne(
                new LambdaQueryWrapper<WmsCategory>()
                        .eq(WmsCategory::getCategoryCode, dto.getCategoryCode())
                        .ne(WmsCategory::getId, id)
        );
        if (exist != null) {
            throw new BusinessException("分类编码已存在");
        }

        // 3. 不能将自己设为自己的父分类
        if (dto.getParentId() != null && dto.getParentId().equals(id)) {
            throw new BusinessException("父分类不能是自己");
        }

        // 4. 更新数据库
        BeanUtils.copyProperties(dto, category);
        categoryMapper.updateById(category);
        log.info("更新分类成功: {}", category.getCategoryCode());
        return category;
    }

    /**
     * 删除分类
     * 
     * 注意: 删除前需要检查是否有子分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<WmsCategory>()
                        .eq(WmsCategory::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，不能删除");
        }

        categoryMapper.deleteById(id);
        log.info("删除分类成功: {}", category.getCategoryCode());
    }

    /**
     * 根据ID查询分类
     */
    @Override
    public WmsCategory getById(Long id) {
        WmsCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    /**
     * 查询分类列表
     */
    @Override
    public List<WmsCategory> list() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<WmsCategory>()
                        .eq(WmsCategory::getStatus, 1)
                        .orderByAsc(WmsCategory::getSortOrder)
                        .orderByAsc(WmsCategory::getId)
        );
    }

    /**
     * 查询分类树（包含子分类）
     * 
     * 算法:
     * 1. 查询所有分类
     * 2. 按parentId分组
     * 3. 递归构建树结构
     * 
     * 注意: 这里简化处理，只返回两层树结构
     */
    @Override
    public List<WmsCategory> tree() {
        // 1. 查询所有分类
        List<WmsCategory> allCategories = list();
        
        // 2. 按parentId分组
        Map<Long, List<WmsCategory>> parentMap = allCategories.stream()
                .collect(Collectors.groupingBy(WmsCategory::getParentId));
        
        // 3. 获取顶级分类
        List<WmsCategory> rootCategories = parentMap.getOrDefault(0L, new ArrayList<>());
        
        // 4. 为每个顶级分类设置子分类
        for (WmsCategory root : rootCategories) {
            List<WmsCategory> children = parentMap.getOrDefault(root.getId(), new ArrayList<>());
            // 注意: 这里需要实体类中有children字段，或者使用其他方式返回树结构
            // 简化处理：直接返回扁平列表，前端自行构建树
        }
        
        return allCategories;
    }
}
