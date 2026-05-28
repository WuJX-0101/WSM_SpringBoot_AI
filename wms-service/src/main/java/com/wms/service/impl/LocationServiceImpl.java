package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.core.PageResult;
import com.wms.common.exception.BusinessException;
import com.wms.dao.mapper.WmsLocationMapper;
import com.wms.dao.mapper.WmsWarehouseMapper;
import com.wms.model.dto.LocationDTO;
import com.wms.model.entity.WmsLocation;
import com.wms.model.entity.WmsWarehouse;
import com.wms.model.vo.LocationVO;
import com.wms.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库位服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final WmsLocationMapper locationMapper;
    private final WmsWarehouseMapper warehouseMapper;

    /**
     * 创建库位
     * 
     * 流程:
     * 1. 检查库位编码唯一性
     * 2. 复制DTO属性到实体
     * 3. 插入数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsLocation create(LocationDTO dto) {
        // 1. 检查库位编码唯一性
        WmsLocation exist = locationMapper.selectOne(
                new LambdaQueryWrapper<WmsLocation>()
                        .eq(WmsLocation::getLocationCode, dto.getLocationCode())
        );
        if (exist != null) {
            throw new BusinessException("库位编码已存在");
        }

        // 2. 构建实体
        WmsLocation location = new WmsLocation();
        BeanUtils.copyProperties(dto, location);
        location.setStatus(1);

        // 3. 插入数据库
        locationMapper.insert(location);
        log.info("创建库位成功: {}", location.getLocationCode());
        return location;
    }

    /**
     * 更新库位
     * 
     * 流程:
     * 1. 检查库位是否存在
     * 2. 检查库位编码唯一性（排除自身）
     * 3. 更新数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsLocation update(Long id, LocationDTO dto) {
        // 1. 检查库位是否存在
        WmsLocation location = locationMapper.selectById(id);
        if (location == null) {
            throw new BusinessException("库位不存在");
        }

        // 2. 检查库位编码唯一性（排除自身）
        WmsLocation exist = locationMapper.selectOne(
                new LambdaQueryWrapper<WmsLocation>()
                        .eq(WmsLocation::getLocationCode, dto.getLocationCode())
                        .ne(WmsLocation::getId, id)
        );
        if (exist != null) {
            throw new BusinessException("库位编码已存在");
        }

        // 3. 更新数据库
        BeanUtils.copyProperties(dto, location);
        locationMapper.updateById(location);
        log.info("更新库位成功: {}", location.getLocationCode());
        return location;
    }

    /**
     * 删除库位
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsLocation location = locationMapper.selectById(id);
        if (location == null) {
            throw new BusinessException("库位不存在");
        }
        locationMapper.deleteById(id);
        log.info("删除库位成功: {}", location.getLocationCode());
    }

    /**
     * 根据ID查询库位
     */
    @Override
    public WmsLocation getById(Long id) {
        WmsLocation location = locationMapper.selectById(id);
        if (location == null) {
            throw new BusinessException("库位不存在");
        }
        return location;
    }

    /**
     * 分页查询库位列表
     *
     * @param page        页码
     * @param size        每页数量
     * @param keyword     搜索关键词（匹配库位编码或名称）
     * @param warehouseId 仓库ID（可选）
     */
    @Override
    public PageResult<LocationVO> list(int page, int size, String keyword, Long warehouseId) {
        LambdaQueryWrapper<WmsLocation> wrapper = new LambdaQueryWrapper<>();

        // 按仓库筛选
        if (warehouseId != null) {
            wrapper.eq(WmsLocation::getWarehouseId, warehouseId);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(WmsLocation::getLocationCode, keyword)
                    .or()
                    .like(WmsLocation::getLocationName, keyword)
            );
        }

        wrapper.orderByDesc(WmsLocation::getGmtCreate);

        Page<WmsLocation> result = locationMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 批量查询关联数据
        List<WmsLocation> records = result.getRecords();
        List<LocationVO> voList = convertToLocationVO(records);
        
        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    /**
     * 将库位实体列表转换为库位VO列表
     * 
     * 关联查询仓库名称
     */
    private List<LocationVO> convertToLocationVO(List<WmsLocation> locations) {
        if (locations.isEmpty()) {
            return List.of();
        }

        // 批量查询仓库
        List<Long> warehouseIds = locations.stream()
                .map(WmsLocation::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WmsWarehouse> warehouseMap = warehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 转换为VO
        return locations.stream().map(location -> {
            LocationVO vo = new LocationVO();
            // 复制基础属性
            vo.setId(location.getId());
            vo.setWarehouseId(location.getWarehouseId());
            vo.setLocationCode(location.getLocationCode());
            vo.setLocationName(location.getLocationName());
            vo.setLocationType(location.getLocationType());
            vo.setArea(location.getArea());
            vo.setShelf(location.getShelf());
            vo.setLayer(location.getLayer());
            vo.setPosition(location.getPosition());
            vo.setCapacity(location.getCapacity());
            vo.setStatus(location.getStatus());
            vo.setGmtCreate(location.getGmtCreate());
            vo.setGmtModified(location.getGmtModified());
            
            // 设置关联名称
            WmsWarehouse warehouse = warehouseMap.get(location.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询仓库下的库位列表
     * 
     * @param warehouseId 仓库ID
     * @return 库位列表
     */
    @Override
    public List<WmsLocation> listByWarehouseId(Long warehouseId) {
        return locationMapper.selectList(
                new LambdaQueryWrapper<WmsLocation>()
                        .eq(WmsLocation::getWarehouseId, warehouseId)
                        .eq(WmsLocation::getStatus, 1)
                        .orderByAsc(WmsLocation::getLocationCode)
        );
    }
}
