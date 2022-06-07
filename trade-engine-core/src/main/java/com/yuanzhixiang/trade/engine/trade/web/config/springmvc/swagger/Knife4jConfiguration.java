package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.swagger;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 访问的地址 http://localhost/doc.html#/home
 *
 * @author zhixiang.yuan
 * @since 2021/02/27 17:42:39
 */
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                //.title("swagger-bootstrap-ui-demo RESTful APIs")
                .version("1.0")
                .build())
            //分组名称
            .select()
            //这里指定Controller扫描包路径
            .apis(RequestHandlerSelectors.basePackage("com.yuanzhixiang.trade.engine"))
            .paths(PathSelectors.any())
            .build();
    }
}
