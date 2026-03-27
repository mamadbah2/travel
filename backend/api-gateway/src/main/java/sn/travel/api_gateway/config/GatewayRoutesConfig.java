package sn.travel.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Value("${gateway.routes.auth-service-uri:http://localhost:8081}")
    private String authServiceUri;

    @Value("${gateway.routes.travel-service-uri:http://localhost:8082}")
    private String travelServiceUri;

    @Value("${gateway.routes.payment-service-uri:http://localhost:8083}")
    private String paymentServiceUri;

    @Value("${gateway.routes.rec-service-uri:http://localhost:8086}")
    private String recServiceUri;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri(authServiceUri))
                .route("travel-service", r -> r.path("/api/v1/travels/**")
                        .uri(travelServiceUri))
                .route("payment-service", r -> r.path("/api/v1/payments/**")
                        .uri(paymentServiceUri))
                .route("rec-service-recommendations", r -> r.path("/api/v1/recommendations/**")
                        .uri(recServiceUri))
                .route("rec-service-feedbacks", r -> r.path("/api/v1/feedbacks/**")
                        .uri(recServiceUri))
                .route("rec-service-reports", r -> r.path("/api/v1/reports/**")
                        .uri(recServiceUri))
                .build();
    }
}
