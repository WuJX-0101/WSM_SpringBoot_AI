package com.wms.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 
 * 功能：配置Redis序列化方式
 * 默认的JDK序列化会导致Redis中存储乱码，改为JSON序列化
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * 
     * 序列化配置：
     * - Key: StringRedisSerializer（字符串序列化，可读性好）
     * - Value: GenericJackson2JsonRedisSerializer（JSON序列化，支持对象存储）
     * 
     * 使用示例：
     * redisTemplate.opsForValue().set("key", object);
     * redisTemplate.opsForValue().get("key");
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key序列化：String类型
        template.setKeySerializer(new StringRedisSerializer());
        // Value序列化：JSON类型
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash Key序列化：String类型
        template.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value序列化：JSON类型
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
