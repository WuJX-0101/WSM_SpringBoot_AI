package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.ProductDTO;
import com.wms.model.entity.WmsProduct;
import com.wms.model.vo.ProductVO;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 创建商品
     */
    WmsProduct create(ProductDTO dto);

    /**
     * 更新商品
     */
    WmsProduct update(Long id, ProductDTO dto);

    /**
     * 删除商品
     */
    void delete(Long id);

    /**
     * 根据ID查询商品
     */
    WmsProduct getById(Long id);

    /**
     * 分页查询商品列表
     */
    PageResult<ProductVO> list(int page, int size, String keyword);
}
