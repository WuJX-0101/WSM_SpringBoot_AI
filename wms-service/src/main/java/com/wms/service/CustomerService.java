package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.CustomerDTO;
import com.wms.model.entity.WmsCustomer;

/**
 * 客户服务接口
 */
public interface CustomerService {

    /**
     * 创建客户
     */
    WmsCustomer create(CustomerDTO dto);

    /**
     * 更新客户
     */
    WmsCustomer update(Long id, CustomerDTO dto);

    /**
     * 删除客户
     */
    void delete(Long id);

    /**
     * 根据ID查询客户
     */
    WmsCustomer getById(Long id);

    /**
     * 分页查询客户列表
     */
    PageResult<WmsCustomer> list(int page, int size, String keyword);
}
