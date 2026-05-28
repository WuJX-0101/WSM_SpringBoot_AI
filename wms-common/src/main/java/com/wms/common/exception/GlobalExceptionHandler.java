package com.wms.common.exception;

import com.wms.common.core.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 使用 @RestControllerAdvice 拦截所有Controller层异常
 * 统一返回 R<Void> 格式的错误响应
 * 
 * 异常处理优先级:
 * 1. BusinessException - 业务异常（自定义）
 * 2. MethodArgumentNotValidException - 参数校验异常（@Valid）
 * 3. BindException - 参数绑定异常
 * 4. Exception - 兜底异常（未知错误）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * BusinessException 是自定义的业务异常类
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 触发条件: @Valid 校验失败（如 @NotBlank, @Size 等）
     * 返回400状态码
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleValidException(MethodArgumentNotValidException e) {
        // 提取所有校验失败的错误信息，用逗号分隔
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}", message);
        return R.fail(400, message);
    }

    /**
     * 处理参数绑定异常
     * 触发条件: 请求参数类型不匹配（如字符串转数字失败）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {}", message);
        return R.fail(400, message);
    }

    /**
     * 兜底异常处理
     * 捕获所有未处理的异常，返回500错误
     * 注意: 生产环境不应暴露详细错误信息给前端
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail("系统内部错误");
    }
}
