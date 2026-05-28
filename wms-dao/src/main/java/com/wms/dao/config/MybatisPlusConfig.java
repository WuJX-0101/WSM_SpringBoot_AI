package com.wms.dao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 
 * 功能：配置分页插件
 * MyBatis-Plus的分页功能需要手动配置拦截器才能生效
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置分页拦截器
     * 
     * PaginationInnerInterceptor: 分页插件
     * DbType.MYSQL: 指定数据库类型（不同数据库分页SQL不同）
     * 
     * 使用方式：
     * Page<User> page = new Page<>(1, 10); // 第1页，每页10条
     * userMapper.selectPage(page, wrapper);
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
