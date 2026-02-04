package sn.travel.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import sn.travel.auth_service.config.JwtProperties;

/**
 * Main entry point for the Auth Service application.
 * This service handles user authentication, authorization, and identity management
 * for the Travel platform.
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}

