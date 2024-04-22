package com.narvane.gateway.config;

import com.narvane.gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    @Value("${services.my-storage.host}")
    private String storageServiceUrl;

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("login", r -> r
                        .path("/login")
                        .uri("no-op://localhost"))
                .route("register", r -> r
                        .path("/register")
                        .uri("no-op://localhost"))
                .route("my-storage-service", p -> p
                        .path("/my-storage/**")
                        .filters(f -> f.filter(filter))
                        .uri(storageServiceUrl))
                .build();
    }
}
