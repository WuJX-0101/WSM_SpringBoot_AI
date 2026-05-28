package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.OutboundOrderDTO;
import com.wms.model.entity.WmsOutboundOrder;
import com.wms.model.vo.OutboundOrderVO;

/**
 * 出库单服务接口
 */
public interface OutboundOrderService {

    /**
     * 创建出库单
     */
    WmsOutboundOrder create(OutboundOrderDTO dto);

    /**
     * 审核出库单
     */
    void audit(Long id);

    /**
     * 执行出库（更新库存）
     */
    void execute(Long id);

    /**
     * 取消出库单
     */
    void cancel(Long id);

    /**
     * 根据ID查询出库单（包含明细和商品信息）
     */
    OutboundOrderVO getById(Long id);

    /**
     * 分页查询出库单列表
     */
    PageResult<OutboundOrderVO> list(int page, int size, String keyword, Integer status);
}
