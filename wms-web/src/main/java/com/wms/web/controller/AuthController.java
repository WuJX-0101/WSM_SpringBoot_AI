package com.wms.web.controller;

import com.wms.common.core.PageResult;
import com.wms.common.core.R;
import com.wms.model.dto.EmployeeDTO;
import com.wms.model.dto.EmployeeUpdateDTO;
import com.wms.model.dto.LoginDTO;
import com.wms.model.dto.RegisterDTO;
import com.wms.model.vo.LoginVO;
import com.wms.model.vo.UserVO;
import com.wms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return R.ok(loginVO, "登录成功");
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return R.ok(null, "注册成功");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<UserVO> getCurrentUserInfo() {
        UserVO userVO = authService.getCurrentUserInfo();
        return R.ok(userVO);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok(null, "退出成功");
    }

    @Operation(summary = "员工列表")
    @GetMapping("/employee/list")
    public R<PageResult<UserVO>> listEmployees(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResult<UserVO> result = authService.listEmployees(page, size, keyword);
        return R.ok(result);
    }

    @Operation(summary = "创建员工账户")
    @PostMapping("/employee")
    public R<Void> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        authService.createEmployee(dto);
        return R.ok(null, "创建成功");
    }

    @Operation(summary = "修改员工信息")
    @PutMapping("/employee/{id}")
    public R<Void> updateEmployee(
            @Parameter(description = "员工ID") @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {
        authService.updateEmployee(id, dto);
        return R.ok(null, "修改成功");
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/employee/{id}")
    public R<Void> deleteEmployee(@Parameter(description = "员工ID") @PathVariable Long id) {
        authService.deleteEmployee(id);
        return R.ok(null, "删除成功");
    }
}
