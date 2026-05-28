package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.InboundOrderDTO;
import com.wms.model.entity.WmsInboundOrder;
import com.wms.model.vo.InboundOrderVO;

/**
 * 入库单服务接口
 */
public interface InboundOrderService {

    /**
     * 创建入库单
     */
    WmsInboundOrder create(InboundOrderDTO dto);

    /**
     * 审核入库单
     */
    void audit(Long id);

    /**
     * 执行入库（更新库存）
     */
    void execute(Long id);

    /**
     * 取消入库单
     */
    void cancel(Long id);

    /**
     * 根据ID查询入库单（包含明细和商品信息）
     */
    InboundOrderVO getById(Long id);

    /**
     * 分页查询入库单列表
     */
    PageResult<InboundOrderVO> list(int page, int size, String keyword, Integer status);
}
