package com.gydx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger的配置类
 */
@Configuration
public class SwaggerConfig {

    // 配置swagger
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gydx.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    // api文档详细信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Api Document For Voice Floater")
                .contact(new Contact("Zhang", "www.baidu.com", "1050367616@qq.com"))
                .version("1.0")
                .description("Api Document For Voice Floater")
                .build();
    }

}
