package com.antplagsystem.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("file-storage", r -> r
                        .path("/api/works/**")
                        .uri("http://file-storing-service:8081"))
                .route("file-analysis", r -> r
                        .path("/api/reports/**")
                        .uri("http://file-analysis-service:8082"))
                .build();
    }
}