package sn.travel.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Reactive Security Configuration for API Gateway.
 * <p>
 * Configures:
 * - Public access for authentication endpoints
 * - JWT validation for protected routes
 * - CORS policy for development
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the reactive security filter chain.
     * <p>
     * Public endpoints:
     * - OPTIONS requests (CORS preflight)
     * - /api/v1/auth/** (Login, Register, Token refresh)
     * - /actuator/** (Health checks, metrics)
     * <p>
     * Protected endpoints:
     * - All other routes require a valid JWT token
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Allow CORS preflight requests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public authentication endpoints
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        // Actuator endpoints for monitoring
                        .pathMatchers("/actuator/**").permitAll()
                        // Swagger/OpenAPI documentation
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        // All other requests require authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // JWT validation is configured via application.properties
                            // spring.security.oauth2.resourceserver.jwt.jwk-set-uri
                        })
                )
                .build();
    }

    /**
     * CORS configuration for development.
     * <p>
     * In production, restrict allowed origins to specific domains.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins for development (restrict in production)
        configuration.setAllowedOrigins(List.of("*"));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name()
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Expose authorization header for frontend
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        
        // Disable credentials when using wildcard origin
        configuration.setAllowCredentials(false);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
