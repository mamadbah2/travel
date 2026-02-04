package sn.travel.auth_service.web.dto.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for token refresh request.
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
