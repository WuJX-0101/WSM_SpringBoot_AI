package com.wms.web.controller;

import com.wms.common.core.R;
import com.wms.model.dto.CategoryDTO;
import com.wms.model.entity.WmsCategory;
import com.wms.service.CategoryService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理接口
 */
@Tag(name = "商品分类管理接口")
@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 创建分类
     */
    @Operation(summary = "创建分类")
    @PostMapping
    @SaCheckPermission("category:create")
    public R<WmsCategory> create(@Valid @RequestBody CategoryDTO dto) {
        WmsCategory category = categoryService.create(dto);
        return R.ok(category, "创建成功");
    }

    /**
     * 更新分类
     */
    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @SaCheckPermission("category:edit")
    public R<WmsCategory> update(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto) {
        WmsCategory category = categoryService.update(id, dto);
        return R.ok(category, "更新成功");
    }

    /**
     * 删除分类
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @SaCheckPermission("category:delete")
    public R<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.delete(id);
        return R.ok(null, "删除成功");
    }

    /**
     * 查询分类详情
     */
    @Operation(summary = "查询分类详情")
    @GetMapping("/{id}")
    @SaCheckPermission("category:view")
    public R<WmsCategory> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        WmsCategory category = categoryService.getById(id);
        return R.ok(category);
    }

    /**
     * 查询分类列表
     */
    @Operation(summary = "查询分类列表")
    @GetMapping("/list")
    @SaCheckPermission("category:view")
    public R<List<WmsCategory>> list() {
        List<WmsCategory> result = categoryService.list();
        return R.ok(result);
    }

    /**
     * 查询分类树
     */
    @Operation(summary = "查询分类树")
    @GetMapping("/tree")
    @SaCheckPermission("category:view")
    public R<List<WmsCategory>> tree() {
        List<WmsCategory> result = categoryService.tree();
        return R.ok(result);
    }
}
