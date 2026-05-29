package com.wms.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Knife4j API文档配置
 * 仅在开发环境(dev)启用，生产环境(prod)禁用
 */
@Configuration
@Profile("dev")
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("仓储物流管理系统 API")
                        .description("基于SpringBoot+AI的仓储物流管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WMS")
                                .email("admin@wms.com")))
                .schemaRequirement("Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization"))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
