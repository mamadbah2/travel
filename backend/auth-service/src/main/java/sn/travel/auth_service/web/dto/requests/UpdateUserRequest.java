package sn.travel.auth_service.web.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;

/**
 * DTO for updating user information.
 */
public record UpdateUserRequest(
        @Email(message = "Invalid email format")
        String email,

        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phoneNumber,

        UserRole role,

        UserStatus status,

        Float performanceScore
) {}
