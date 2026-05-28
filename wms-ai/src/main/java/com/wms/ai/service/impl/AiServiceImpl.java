package com.wms.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.ai.service.AiService;
import com.wms.dao.mapper.*;
import com.wms.model.entity.*;
import com.wms.model.vo.AnomalyDetectionVO;
import com.wms.model.vo.DemandForecastVO;
import com.wms.model.vo.ReplenishmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI服务实现类
 * 
 * 使用Spring AI + DeepSeek实现需求预测、智能补货、异常检测
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient.Builder chatClientBuilder;
    private final WmsProductMapper productMapper;
    private final WmsInventoryMapper inventoryMapper;
    private final WmsInboundOrderItemMapper inboundOrderItemMapper;
    private final WmsOutboundOrderItemMapper outboundOrderItemMapper;
    private final WmsSupplierMapper supplierMapper;
    private final WmsWarehouseMapper warehouseMapper;
    private final ObjectMapper objectMapper;

    /**
     * 需求预测
     * 
     * 流程:
     * 1. 获取商品信息
     * 2. 获取历史销售数据
     * 3. 构建AI提示词
     * 4. 调用DeepSeek进行预测
     * 5. 解析结果返回
     */
    @Override
    public DemandForecastVO forecastDemand(Long productId, Integer forecastDays) {
        // 1. 获取商品信息
        WmsProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 获取历史销售数据（最近30天）
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<WmsOutboundOrderItem> outboundItems = outboundOrderItemMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundOrderItem>()
                        .eq(WmsOutboundOrderItem::getProductId, productId)
                        .ge(WmsOutboundOrderItem::getGmtCreate, startDate)
                        .eq(WmsOutboundOrderItem::getIsDeleted, 0)
        );

        // 按日期统计销量
        Map<String, Integer> dailySales = outboundItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getGmtCreate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.summingInt(WmsOutboundOrderItem::getQuantity)
                ));

        // 3. 获取当前库存
        int currentStock = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getProductId, productId)
                        .eq(WmsInventory::getIsDeleted, 0)
        ).stream().mapToInt(WmsInventory::getQuantity).sum();

        // 计算日均销量
        double avgDailySales = dailySales.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // 4. 构建历史数据描述
        StringBuilder historyDesc = new StringBuilder();
        historyDesc.append("商品信息:\n");
        historyDesc.append("- 商品名称: ").append(product.getProductName()).append("\n");
        historyDesc.append("- 商品编码: ").append(product.getProductCode()).append("\n");
        historyDesc.append("- 规格: ").append(product.getSpecification() != null ? product.getSpecification() : "无").append("\n");
        historyDesc.append("- 销售单价: ").append(product.getSalePrice()).append("元\n");
        historyDesc.append("- 采购单价: ").append(product.getPurchasePrice() != null ? product.getPurchasePrice() : "未知").append("元\n");
        historyDesc.append("- 安全库存: ").append(product.getSafetyStock() != null ? product.getSafetyStock() : 0).append("\n");
        historyDesc.append("- 当前库存: ").append(currentStock).append("\n");
        historyDesc.append("- 日均销量(近30天): ").append(String.format("%.1f", avgDailySales)).append("\n\n");

        historyDesc.append("最近30天每日销售数据:\n");
        for (Map.Entry<String, Integer> entry : dailySales.entrySet()) {
            historyDesc.append(entry.getKey()).append(": ").append(entry.getValue()).append("件\n");
        }

        // 5. 构建AI提示词
        String prompt = String.format("""
                你是一个专业的仓储需求预测分析师。请根据以下商品的历史销售数据，预测未来%d天的**总需求量**。

                %s

                预测要求:
                1. forecastQuantity: 未来%d天的**总需求量**（整数），不是日均需求量
                2. confidence: 置信度（0-100的整数），根据数据波动性判断
                3. explanation: 详细预测说明（100-200字），包含:
                   - 销售趋势分析（上升/下降/平稳）
                   - 季节性因素考虑
                   - 预测方法说明
                   - 风险提示
                4. suggestion: 补货建议（50-100字），包含具体行动建议

                请用JSON格式返回，只返回JSON，不要其他内容。
                示例格式:
                {
                  "forecastQuantity": 500,
                  "confidence": 75,
                  "explanation": "根据近30天销售数据分析...",
                  "suggestion": "建议在XX天内补货..."
                }
                """, forecastDays, historyDesc.toString(), forecastDays);

        // 5. 调用AI
        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 6. 解析结果
        DemandForecastVO result = new DemandForecastVO();
        result.setProductId(productId);
        result.setProductName(product.getProductName());
        result.setForecastDays(forecastDays);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.trim());
            result.setForecastQuantity(jsonNode.has("forecastQuantity") ? jsonNode.get("forecastQuantity").asInt() : 0);
            result.setConfidence(jsonNode.has("confidence") ? jsonNode.get("confidence").asInt() : 0);
            result.setExplanation(jsonNode.has("explanation") ? jsonNode.get("explanation").asText() : "");
            result.setSuggestion(jsonNode.has("suggestion") ? jsonNode.get("suggestion").asText() : "");
            // 兼容旧格式
            if (result.getExplanation().isEmpty() && jsonNode.has("basis")) {
                result.setExplanation(jsonNode.get("basis").asText());
            }
        } catch (Exception e) {
            log.error("解析AI响应失败: {}", response, e);
            result.setForecastQuantity(0);
            result.setConfidence(0);
            result.setExplanation("AI响应解析失败，建议人工审核");
            result.setSuggestion("建议人工审核");
        }

        // 设置 basis（兼容）
        result.setBasis(result.getExplanation());

        // 计算建议补货时间
        result.setReplenishDate(calculateReplenishDate(product, result.getForecastQuantity(), dailySales));

        // 构建历史数据
        List<DemandForecastVO.HistoryData> historyDataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dailySales.entrySet()) {
            DemandForecastVO.HistoryData historyData = new DemandForecastVO.HistoryData();
            historyData.setDate(entry.getKey());
            historyData.setQuantity(entry.getValue());
            historyDataList.add(historyData);
        }
        result.setHistoryData(historyDataList);

        // 生成预测趋势数据
        result.setForecastData(generateForecastData(result.getForecastQuantity(), forecastDays));

        return result;
    }

    /**
     * 计算建议补货时间
     * 根据当前库存、日均销量、安全库存推算何时需要补货
     */
    private String calculateReplenishDate(WmsProduct product, Integer forecastQuantity, Map<String, Integer> dailySales) {
        // 计算日均销量
        double avgDailySales = dailySales.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // 如果没有销售数据，使用预测数量除以预测天数
        if (avgDailySales == 0 && forecastQuantity != null && forecastQuantity > 0) {
            avgDailySales = forecastQuantity / 7.0;
        }

        // 获取当前库存
        int currentStock = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getProductId, product.getId())
                        .eq(WmsInventory::getIsDeleted, 0)
        ).stream().mapToInt(WmsInventory::getQuantity).sum();

        // 安全库存
        int safetyStock = product.getSafetyStock() != null ? product.getSafetyStock() : 0;

        // 计算多少天后需要补货（当库存降至安全库存时）
        if (avgDailySales > 0 && currentStock > safetyStock) {
            int daysUntilReplenish = (int) ((currentStock - safetyStock) / avgDailySales);
            LocalDate replenishDate = LocalDate.now().plusDays(daysUntilReplenish);
            return replenishDate.toString();
        }

        // 如果当前库存已低于安全库存，建议立即补货
        if (currentStock <= safetyStock) {
            return LocalDate.now().toString();
        }

        return LocalDate.now().plusDays(7).toString();
    }

    /**
     * 生成预测趋势数据
     * 基于预测总量生成每日预测值（用于图表展示）
     */
    private List<DemandForecastVO.ForecastData> generateForecastData(Integer forecastQuantity, Integer forecastDays) {
        List<DemandForecastVO.ForecastData> forecastDataList = new ArrayList<>();
        if (forecastQuantity == null || forecastDays == null || forecastDays <= 0) {
            return forecastDataList;
        }

        // 计算日均预测量
        int dailyForecast = forecastQuantity / forecastDays;
        int remainder = forecastQuantity % forecastDays;

        LocalDate startDate = LocalDate.now().plusDays(1);
        for (int i = 0; i < forecastDays; i++) {
            DemandForecastVO.ForecastData data = new DemandForecastVO.ForecastData();
            data.setDate(startDate.plusDays(i).toString());
            // 将余数分配到前几天
            data.setValue(dailyForecast + (i < remainder ? 1 : 0));
            forecastDataList.add(data);
        }

        return forecastDataList;
    }

    /**
     * 智能补货建议
     *
     * 流程:
     * 1. 获取所有商品库存信息
     * 2. 筛选低于安全库存的商品
     * 3. 构建AI提示词
     * 4. 调用DeepSeek生成补货建议
     * 5. 解析结果返回
     */
    @Override
    public ReplenishmentVO getReplenishmentSuggestion(Long warehouseId) {
        // 1. 获取库存信息
        LambdaQueryWrapper<WmsInventory> inventoryWrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            inventoryWrapper.eq(WmsInventory::getWarehouseId, warehouseId);
        }
        inventoryWrapper.gt(WmsInventory::getQuantity, 0);
        List<WmsInventory> inventories = inventoryMapper.selectList(inventoryWrapper);

        // 获取商品信息
        List<Long> productIds = inventories.stream()
                .map(WmsInventory::getProductId)
                .distinct()
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            ReplenishmentVO result = new ReplenishmentVO();
            result.setItems(new ArrayList<>());
            result.setTotalQuantity(0);
            result.setTotalAmount(0.0);
            result.setOverallSuggestion("暂无库存数据");
            return result;
        }

        List<WmsProduct> products = productMapper.selectBatchIds(productIds);
        Map<Long, WmsProduct> productMap = products.stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 按商品汇总库存
        Map<Long, Integer> stockMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        WmsInventory::getProductId,
                        Collectors.summingInt(WmsInventory::getQuantity)
                ));

        // 2. 筛选低于安全库存的商品
        List<WmsProduct> lowStockProducts = products.stream()
                .filter(p -> {
                    int currentStock = stockMap.getOrDefault(p.getId(), 0);
                    return currentStock < p.getSafetyStock();
                })
                .collect(Collectors.toList());

        // 3. 构建AI提示词
        StringBuilder stockInfo = new StringBuilder();
        stockInfo.append("库存低于安全库存的商品:\n");
        for (WmsProduct product : lowStockProducts) {
            int currentStock = stockMap.getOrDefault(product.getId(), 0);
            stockInfo.append(String.format("- %s (编码: %s): 当前库存 %d, 安全库存 %d, 差额 %d\n",
                    product.getProductName(),
                    product.getProductCode(),
                    currentStock,
                    product.getSafetyStock(),
                    product.getSafetyStock() - currentStock));
        }

        // 获取供应商信息
        List<WmsSupplier> suppliers = supplierMapper.selectList(
                new LambdaQueryWrapper<WmsSupplier>()
                        .eq(WmsSupplier::getStatus, 1)
                        .eq(WmsSupplier::getIsDeleted, 0)
        );

        StringBuilder supplierInfo = new StringBuilder();
        supplierInfo.append("\n可用供应商:\n");
        for (WmsSupplier supplier : suppliers) {
            supplierInfo.append(String.format("- %s (联系人: %s, 电话: %s)\n",
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getContactPhone()));
        }

        String prompt = String.format("""
                你是一个专业的仓储补货分析师。请根据以下信息生成补货建议。
                
                %s
                
                %s
                
                请用JSON格式返回结果，包含以下字段:
                - overallSuggestion: 整体建议（字符串）
                - items: 补货项数组，每个项包含:
                  - productId: 商品ID
                  - replenishQuantity: 建议补货数量
                  - suggestedSupplier: 建议供应商
                  - urgency: 紧急程度（高/中/低）
                  - reason: 补货原因
                
                只返回JSON，不要其他内容。
                """, stockInfo.toString(), supplierInfo.toString());

        // 4. 调用AI
        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 5. 解析结果
        ReplenishmentVO result = new ReplenishmentVO();
        List<ReplenishmentVO.ReplenishmentItem> items = new ArrayList<>();

        try {
            // 简单解析（实际项目应使用Jackson）
            result.setOverallSuggestion(response);
            
            // 为每个低库存商品生成补货项
            for (WmsProduct product : lowStockProducts) {
                int currentStock = stockMap.getOrDefault(product.getId(), 0);
                int replenishQuantity = product.getSafetyStock() - currentStock + 
                        (product.getMaxStock() - product.getSafetyStock()) / 2;
                
                ReplenishmentVO.ReplenishmentItem item = new ReplenishmentVO.ReplenishmentItem();
                item.setProductId(product.getId());
                item.setProductName(product.getProductName());
                item.setProductCode(product.getProductCode());
                item.setCurrentStock(currentStock);
                item.setSafetyStock(product.getSafetyStock());
                item.setReplenishQuantity(replenishQuantity);
                item.setEstimatedPrice(product.getPurchasePrice() != null ? 
                        product.getPurchasePrice().doubleValue() : 0.0);
                item.setEstimatedAmount(item.getEstimatedPrice() * replenishQuantity);
                
                // 判断紧急程度
                if (currentStock == 0) {
                    item.setUrgency("高");
                    item.setReason("库存为零，急需补货");
                } else if (currentStock < product.getSafetyStock() / 2) {
                    item.setUrgency("高");
                    item.setReason("库存严重不足");
                } else if (currentStock < product.getSafetyStock()) {
                    item.setUrgency("中");
                    item.setReason("库存低于安全库存");
                } else {
                    item.setUrgency("低");
                    item.setReason("建议补货");
                }
                
                // 建议供应商
                if (!suppliers.isEmpty()) {
                    item.setSuggestedSupplier(suppliers.get(0).getSupplierName());
                }
                
                items.add(item);
            }
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            result.setOverallSuggestion("AI响应解析失败，建议人工审核");
        }

        result.setItems(items);
        result.setTotalQuantity(items.stream().mapToInt(ReplenishmentVO.ReplenishmentItem::getReplenishQuantity).sum());
        result.setTotalAmount(items.stream().mapToDouble(ReplenishmentVO.ReplenishmentItem::getEstimatedAmount).sum());

        return result;
    }

    /**
     * 异常检测
     * 
     * 流程:
     * 1. 获取库存和订单数据
     * 2. 检测异常情况（库存异常、订单异常等）
     * 3. 构建AI提示词
     * 4. 调用DeepSeek分析
     * 5. 解析结果返回
     */
    @Override
    public AnomalyDetectionVO detectAnomaly(Long warehouseId) {
        // 1. 获取数据
        LambdaQueryWrapper<WmsInventory> inventoryWrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            inventoryWrapper.eq(WmsInventory::getWarehouseId, warehouseId);
        }
        inventoryWrapper.gt(WmsInventory::getQuantity, 0);
        List<WmsInventory> inventories = inventoryMapper.selectList(inventoryWrapper);

        // 获取商品信息
        List<Long> productIds = inventories.stream()
                .map(WmsInventory::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<WmsProduct> products = productIds.isEmpty() ? new ArrayList<>() : 
                productMapper.selectBatchIds(productIds);
        Map<Long, WmsProduct> productMap = products.stream()
                .collect(Collectors.toMap(WmsProduct::getId, p -> p));

        // 按商品汇总库存
        Map<Long, Integer> stockMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        WmsInventory::getProductId,
                        Collectors.summingInt(WmsInventory::getQuantity)
                ));

        // 2. 检测异常
        List<AnomalyDetectionVO.AnomalyItem> anomalies = new ArrayList<>();

        // 检测库存异常
        for (WmsProduct product : products) {
            int currentStock = stockMap.getOrDefault(product.getId(), 0);
            
            // 库存为零
            if (currentStock == 0) {
                AnomalyDetectionVO.AnomalyItem anomaly = new AnomalyDetectionVO.AnomalyItem();
                anomaly.setType("库存异常");
                anomaly.setLevel("高");
                anomaly.setDescription(String.format("商品【%s】库存为零", product.getProductName()));
                anomaly.setTarget(product.getProductName());
                anomaly.setSuggestion("立即补货");
                anomalies.add(anomaly);
            }
            // 库存低于安全库存
            else if (currentStock < product.getSafetyStock()) {
                AnomalyDetectionVO.AnomalyItem anomaly = new AnomalyDetectionVO.AnomalyItem();
                anomaly.setType("库存异常");
                anomaly.setLevel(currentStock < product.getSafetyStock() / 2 ? "高" : "中");
                anomaly.setDescription(String.format("商品【%s】库存%d低于安全库存%d", 
                        product.getProductName(), currentStock, product.getSafetyStock()));
                anomaly.setTarget(product.getProductName());
                anomaly.setSuggestion("建议补货");
                anomalies.add(anomaly);
            }
            // 库存超过最大库存
            else if (product.getMaxStock() > 0 && currentStock > product.getMaxStock()) {
                AnomalyDetectionVO.AnomalyItem anomaly = new AnomalyDetectionVO.AnomalyItem();
                anomaly.setType("库存异常");
                anomaly.setLevel("低");
                anomaly.setDescription(String.format("商品【%s】库存%d超过最大库存%d", 
                        product.getProductName(), currentStock, product.getMaxStock()));
                anomaly.setTarget(product.getProductName());
                anomaly.setSuggestion("考虑促销或调拨");
                anomalies.add(anomaly);
            }
        }

        // 检测过期商品
        LocalDate today = LocalDate.now();
        for (WmsInventory inventory : inventories) {
            if (inventory.getExpiryDate() != null && inventory.getExpiryDate().isBefore(today)) {
                WmsProduct product = productMap.get(inventory.getProductId());
                String productName = product != null ? product.getProductName() : "未知商品";
                
                AnomalyDetectionVO.AnomalyItem anomaly = new AnomalyDetectionVO.AnomalyItem();
                anomaly.setType("库存异常");
                anomaly.setLevel("高");
                anomaly.setDescription(String.format("商品【%s】批次【%s】已过期", 
                        productName, inventory.getBatchNo()));
                anomaly.setTarget(productName);
                anomaly.setSuggestion("立即处理过期商品");
                anomalies.add(anomaly);
            }
        }

        // 3. 构建结果
        AnomalyDetectionVO result = new AnomalyDetectionVO();
        result.setHasAnomaly(!anomalies.isEmpty());
        result.setAnomalyCount(anomalies.size());
        result.setAnomalies(anomalies);

        if (anomalies.isEmpty()) {
            result.setSuggestion("库存状态正常，无需特殊处理");
        } else {
            long highCount = anomalies.stream().filter(a -> "高".equals(a.getLevel())).count();
            long mediumCount = anomalies.stream().filter(a -> "中".equals(a.getLevel())).count();
            long lowCount = anomalies.stream().filter(a -> "低".equals(a.getLevel())).count();
            
            result.setSuggestion(String.format("发现%d个异常: 高级%d个, 中级%d个, 低级%d个。建议优先处理高级异常。",
                    anomalies.size(), highCount, mediumCount, lowCount));
        }

        return result;
    }

    /**
     * 自然语言查询
     *
     * 流程:
     * 1. 获取系统数据作为上下文（商品、库存、仓库）
     * 2. 构建AI提示词
     * 3. 调用DeepSeek回答
     */
    @Override
    public String chat(String question) {
        // 1. 获取商品信息
        List<WmsProduct> products = productMapper.selectList(
                new LambdaQueryWrapper<WmsProduct>()
                        .eq(WmsProduct::getStatus, 1)
                        .eq(WmsProduct::getIsDeleted, 0)
                        .last("LIMIT 100")
        );

        // 2. 获取库存信息
        List<WmsInventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<WmsInventory>()
                        .eq(WmsInventory::getIsDeleted, 0)
        );

        // 按商品汇总库存
        Map<Long, Integer> stockMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        WmsInventory::getProductId,
                        Collectors.summingInt(WmsInventory::getQuantity)
                ));

        // 3. 获取仓库信息
        List<WmsWarehouse> warehouses = warehouseMapper.selectList(
                new LambdaQueryWrapper<WmsWarehouse>()
                        .eq(WmsWarehouse::getStatus, 1)
                        .eq(WmsWarehouse::getIsDeleted, 0)
        );

        // 4. 构建上下文
        StringBuilder context = new StringBuilder();
        context.append("=== 系统数据概览 ===\n");
        context.append("商品种类: ").append(products.size()).append("\n");
        context.append("仓库数量: ").append(warehouses.size()).append("\n\n");

        // 商品库存详情（包含安全库存）
        context.append("=== 商品库存详情 ===\n");
        for (WmsProduct product : products) {
            int currentStock = stockMap.getOrDefault(product.getId(), 0);
            int safetyStock = product.getSafetyStock() != null ? product.getSafetyStock() : 0;
            String stockStatus = currentStock == 0 ? "【缺货】" :
                    (currentStock < safetyStock ? "【低于安全库存】" : "【正常】");

            context.append("- ").append(product.getProductName())
                    .append(" (编码: ").append(product.getProductCode())
                    .append("): 当前库存=").append(currentStock)
                    .append(", 安全库存=").append(safetyStock)
                    .append(", 销售价=").append(product.getSalePrice()).append("元")
                    .append(stockStatus).append("\n");
        }

        // 仓库列表
        context.append("\n=== 仓库列表 ===\n");
        for (WmsWarehouse warehouse : warehouses) {
            context.append("- ").append(warehouse.getWarehouseName())
                    .append(" (编码: ").append(warehouse.getWarehouseCode())
                    .append(", 地址: ").append(warehouse.getAddress())
                    .append(")\n");
        }

        // 5. 构建提示词
        String prompt = String.format("""
                你是一个专业的仓储管理系统助手。请根据以下系统数据回答用户的问题。

                %s

                用户问题: %s

                回答要求:
                1. 基于提供的数据进行分析，给出具体的数字和结论
                2. 如果涉及补货建议，根据安全库存和当前库存的对比来判断
                3. 回答要专业、简洁、有条理
                4. 如果数据中没有相关信息，请明确说明
                """, context.toString(), question);

        // 6. 调用AI
        ChatClient chatClient = chatClientBuilder.build();
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
