package sn.travel.auth_service.web.controllers.implementation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.services.AuthService;
import sn.travel.auth_service.services.UserService;
import sn.travel.auth_service.web.controllers.UserController;
import sn.travel.auth_service.web.dto.requests.ChangePasswordRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.MessageResponse;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;
import sn.travel.auth_service.web.mappers.UserMapper;

import java.util.UUID;

/**
 * REST Controller implementation for user management endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User profile and administration endpoints")
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    // ==================== Current User Endpoints ====================

    @Override
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's profile")
    public UserResponse getCurrentUser() {
        return userMapper.toResponse(authService.getCurrentUser());
    }

    @Override
    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates the authenticated user's profile")
    public UserResponse updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        UUID currentUserId = authService.getCurrentUser().getId();
        // Users cannot change their own role or status
        UpdateUserRequest sanitizedRequest = new UpdateUserRequest(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                null, // role
                null, // status
                null  // performanceScore
        );
        return userService.updateUser(currentUserId, sanitizedRequest);
    }

    @Override
    @PostMapping("/me/change-password")
    @Operation(summary = "Change password", description = "Changes the authenticated user's password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID currentUserId = authService.getCurrentUser().getId();
        userService.changePassword(currentUserId, request);
        return new MessageResponse("Password changed successfully");
    }

    // ==================== Admin Endpoints ====================

    @Override
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Admin only: Returns a user's profile by their ID")
    public UserResponse getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Admin only: Returns paginated list of all users")
    public PageResponse<UserResponse> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Override
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Admin only: Returns paginated list of users filtered by role")
    public PageResponse<UserResponse> getUsersByRole(
            @PathVariable UserRole role,
            @PageableDefault(size = 20) Pageable pageable) {
        return userService.getUsersByRole(role, pageable);
    }

    @Override
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by status", description = "Admin only: Returns paginated list of users filtered by status")
    public PageResponse<UserResponse> getUsersByStatus(
            @PathVariable UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return userService.getUsersByStatus(status, pageable);
    }

    @Override
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users", description = "Admin only: Searches users by name or email")
    public PageResponse<UserResponse> searchUsers(
            @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return userService.searchUsers(search, pageable);
    }

    @Override
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Admin only: Updates a user's profile")
    public UserResponse updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @Override
    @PostMapping("/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ban user", description = "Admin only: Bans a user account")
    public UserResponse banUser(@PathVariable UUID userId) {
        return userService.banUser(userId);
    }

    @Override
    @PostMapping("/{userId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unban user", description = "Admin only: Unbans a user account")
    public UserResponse unbanUser(@PathVariable UUID userId) {
        return userService.unbanUser(userId);
    }

    @Override
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Admin only: Permanently deletes a user account")
    public MessageResponse deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return new MessageResponse("User deleted successfully");
    }

    // ==================== Manager Performance Score ====================

    @PutMapping("/{managerId}/performance-score")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update manager performance score", description = "Admin only: Updates a manager's performance score")
    public UserResponse updatePerformanceScore(
            @PathVariable UUID managerId,
            @RequestParam Float score) {
        return userService.updatePerformanceScore(managerId, score);
    }
}
