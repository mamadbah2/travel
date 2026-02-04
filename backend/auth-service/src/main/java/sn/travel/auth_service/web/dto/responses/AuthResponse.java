package sn.travel.auth_service.web.dto.responses;

/**
 * DTO for authentication response containing JWT tokens.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserResponse user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
