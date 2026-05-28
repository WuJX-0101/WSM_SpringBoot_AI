package com.wms.service.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证授权配置
 * 
 * 功能：
 * 1. 登录认证：拦截所有请求，检查是否已登录
 * 2. 权限校验：基于权限标识进行细粒度控制
 * 
 * 权限标识规范：
 * - 模块:操作（如 warehouse:view, product:manage）
 * - 超级管理员拥有所有权限
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器
     * 
     * SaInterceptor: Sa-Token的拦截器实现
     * - StpUtil.checkLogin(): 检查是否已登录
     * - StpUtil.checkPermission(): 检查是否拥有指定权限
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/api/v1/auth/login",      // 登录接口
                        "/api/v1/auth/register",   // 注册接口
                        "/doc.html",               // Knife4j文档
                        "/webjars/**",             // 静态资源
                        "/v3/api-docs/**",         // OpenAPI文档
                        "/swagger-resources/**",   // Swagger资源
                        "/favicon.ico"             // 网站图标
                );
    }
}
