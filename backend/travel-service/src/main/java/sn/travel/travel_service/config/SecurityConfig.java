package sn.travel.travel_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Security configuration for the travel-service (Resource Server).
 * Uses Spring OAuth2 Resource Server to validate JWT tokens issued by the auth-service.
 * CORS is handled by the API Gateway.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        // Public: browse published travels
                        .requestMatchers(HttpMethod.GET, "/api/v1/travels", "/api/v1/travels/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/travels/search").permitAll()
                        // Manager: create/update/delete own travels
                        .requestMatchers(HttpMethod.POST, "/api/v1/travels").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/travels/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/travels/**").hasAnyRole("MANAGER", "ADMIN")
                        // Manager: manage subscribers
                        .requestMatchers("/api/v1/travels/*/subscribers/**").hasAnyRole("MANAGER", "ADMIN")
                        // Manager: dashboard
                        .requestMatchers("/api/v1/travels/manager/**").hasRole("MANAGER")
                        // Traveler: subscriptions
                        .requestMatchers("/api/v1/subscriptions/**").hasRole("TRAVELER")
                        // Admin: all travels management
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    /**
     * Configures the JWT decoder using the shared HMAC-SHA256 secret
     * (same key used by auth-service to sign tokens).
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Maps JWT claims to Spring Security Authentication:
     * - Principal: "userId" claim (UUID string)
     * - Authorities: "role" claim -> ROLE_{role}
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("userId");
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role != null) {
                return List.of(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return Collections.emptyList();
        });
        return converter;
    }
}
