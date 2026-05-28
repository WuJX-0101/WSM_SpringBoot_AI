package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.ProductDTO;
import com.wms.model.entity.WmsProduct;
import com.wms.model.vo.ProductVO;
import com.wms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理接口
 */
@Tag(name = "商品管理接口")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 创建商品
     */
    @Operation(summary = "创建商品")
    @PostMapping
    public R<WmsProduct> create(@Valid @RequestBody ProductDTO dto) {
        WmsProduct product = productService.create(dto);
        return R.ok(product, "创建成功");
    }

    /**
     * 更新商品
     */
    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public R<WmsProduct> update(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        WmsProduct product = productService.update(id, dto);
        return R.ok(product, "更新成功");
    }

    /**
     * 删除商品
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "商品ID") @PathVariable Long id) {
        productService.delete(id);
        return R.ok(null, "删除成功");
    }

    /**
     * 查询商品详情
     */
    @Operation(summary = "查询商品详情")
    @GetMapping("/{id}")
    public R<WmsProduct> getById(@Parameter(description = "商品ID") @PathVariable Long id) {
        WmsProduct product = productService.getById(id);
        return R.ok(product);
    }

    /**
     * 分页查询商品列表
     */
    @Operation(summary = "分页查询商品列表")
    @GetMapping("/list")
    public R<PageResult<ProductVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResult<ProductVO> result = productService.list(page, size, keyword);
        return R.ok(result);
    }
}
