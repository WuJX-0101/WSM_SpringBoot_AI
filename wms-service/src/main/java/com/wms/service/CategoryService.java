package com.wms.service;

import com.wms.model.dto.CategoryDTO;
import com.wms.model.entity.WmsCategory;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService {

    /**
     * 创建分类
     */
    WmsCategory create(CategoryDTO dto);

    /**
     * 更新分类
     */
    WmsCategory update(Long id, CategoryDTO dto);

    /**
     * 删除分类
     */
    void delete(Long id);

    /**
     * 根据ID查询分类
     */
    WmsCategory getById(Long id);

    /**
     * 查询分类列表
     */
    List<WmsCategory> list();

    /**
     * 查询分类树（包含子分类）
     */
    List<WmsCategory> tree();
}
