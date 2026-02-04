package sn.travel.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT settings.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     */
    private String secret;

    /**
     * Access token expiration time in milliseconds (default: 24 hours).
     */
    private Long expiration = 86400000L;

    /**
     * Refresh token expiration time in milliseconds (default: 7 days).
     */
    private Long refreshExpiration = 604800000L;
}
