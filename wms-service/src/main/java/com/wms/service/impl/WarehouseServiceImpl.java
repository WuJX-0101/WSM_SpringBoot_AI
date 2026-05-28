package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.WmsWarehouseMapper;
import com.wms.model.dto.WarehouseDTO;
import com.wms.model.entity.WmsWarehouse;
import com.wms.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WmsWarehouseMapper warehouseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsWarehouse create(WarehouseDTO dto) {
        WmsWarehouse exist = warehouseMapper.selectOne(
                new LambdaQueryWrapper<WmsWarehouse>()
                        .eq(WmsWarehouse::getWarehouseCode, dto.getWarehouseCode())
        );
        if (exist != null) {
            throw new BusinessException("仓库编码已存在");
        }

        WmsWarehouse warehouse = new WmsWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setStatus(1);
        warehouseMapper.insert(warehouse);
        log.info("创建仓库成功: {}", warehouse.getWarehouseCode());
        return warehouse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsWarehouse update(Long id, WarehouseDTO dto) {
        WmsWarehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }

        WmsWarehouse exist = warehouseMapper.selectOne(
                new LambdaQueryWrapper<WmsWarehouse>()
                        .eq(WmsWarehouse::getWarehouseCode, dto.getWarehouseCode())
                        .ne(WmsWarehouse::getId, id)
        );
        if (exist != null) {
            throw new BusinessException("仓库编码已存在");
        }

        BeanUtils.copyProperties(dto, warehouse);
        warehouseMapper.updateById(warehouse);
        log.info("更新仓库成功: {}", warehouse.getWarehouseCode());
        return warehouse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsWarehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        warehouseMapper.deleteById(id);
        log.info("删除仓库成功: {}", warehouse.getWarehouseCode());
    }

    @Override
    public WmsWarehouse getById(Long id) {
        WmsWarehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        return warehouse;
    }

    /**
     * 分页查询仓库列表
     * 
     * @param page    页码（从1开始）
     * @param size    每页数量
     * @param keyword 搜索关键词（可选，匹配仓库编码或名称）
     * @return 分页结果（包含记录列表、总数、每页数量、当前页码）
     * 
     * LambdaQueryWrapper说明:
     * - like: 模糊查询（LIKE '%keyword%'）
     * - or: 或条件
     * - and: 分组条件（相当于SQL中的括号）
     * - orderByDesc: 降序排序
     */
    @Override
    public PageResult<WmsWarehouse> list(int page, int size, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（匹配仓库编码或名称）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(WmsWarehouse::getWarehouseCode, keyword)
                    .or()
                    .like(WmsWarehouse::getWarehouseName, keyword)
            );
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(WmsWarehouse::getGmtCreate);

        // 执行分页查询
        // MyBatis-Plus的Page对象会自动处理分页SQL
        Page<WmsWarehouse> result = warehouseMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 转换为统一的分页结果格式
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }
}
