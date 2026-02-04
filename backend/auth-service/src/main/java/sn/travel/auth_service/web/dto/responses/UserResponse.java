package sn.travel.auth_service.web.dto.responses;

import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user response data.
 */
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        UserRole role,
        UserStatus status,
        Float performanceScore,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {}
