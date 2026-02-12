package sn.travel.travel_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Security configuration for the travel-service (Resource Server).
 * Validates JWT tokens issued by the auth-service.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
