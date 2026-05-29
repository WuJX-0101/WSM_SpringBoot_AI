package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.SupplierDTO;
import com.wms.model.entity.WmsSupplier;

import java.util.List;

/**
 * 供应商服务接口
 */
public interface SupplierService {

    /**
     * 创建供应商
     */
    WmsSupplier create(SupplierDTO dto);

    /**
     * 更新供应商
     */
    WmsSupplier update(Long id, SupplierDTO dto);

    /**
     * 删除供应商
     */
    void delete(Long id);

    /**
     * 根据ID查询供应商
     */
    WmsSupplier getById(Long id);

    /**
     * 分页查询供应商列表
     */
    PageResult<WmsSupplier> list(int page, int size, String keyword);

    /**
     * 获取所有启用的供应商（用于下拉选择）
     */
    List<WmsSupplier> listAll();
}
