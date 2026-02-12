package sn.travel.travel_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration properties (shared secret with auth-service).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key for JWT signature validation (HMAC-SHA256).
     */
    private String secret;
}
