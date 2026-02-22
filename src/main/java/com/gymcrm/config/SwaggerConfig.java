package com.gymcrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gymcrm.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(List.of(new BasicAuth("basicAuth")))
                .securityContexts(List.of(securityContext()));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(List.of(
                        new SecurityReference("basicAuth", new AuthorizationScope[0])
                ))
                .forPaths(PathSelectors.regex("^(?!/trainees/register|/trainers/register|/swagger-ui.html|/v2/api-docs|/swagger-resources|/webjars/).*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Gym CRM REST API",
                "REST endpoints for trainee, trainer and training management.",
                "1.0",
                "",
                new Contact("GymCRM", "", ""),
                "",
                "",
                Collections.emptyList()
        );
    }
}