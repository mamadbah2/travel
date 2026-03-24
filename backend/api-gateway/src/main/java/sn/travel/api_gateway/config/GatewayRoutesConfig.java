package sn.travel.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://localhost:8081"))
                .route("travel-service", r -> r.path("/api/v1/travels/**")
                        .uri("http://localhost:8082"))
                .route("payment-service", r -> r.path("/api/v1/payments/**")
                        .uri("http://localhost:8083"))
                .route("rec-service-recommendations", r -> r.path("/api/v1/recommendations/**")
                        .uri("http://localhost:8086"))
                .route("rec-service-feedbacks", r -> r.path("/api/v1/feedbacks/**")
                        .uri("http://localhost:8086"))
                .route("rec-service-reports", r -> r.path("/api/v1/reports/**")
                        .uri("http://localhost:8086"))
                .build();
    }
}
