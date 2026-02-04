package sn.travel.auth_service.web.controllers;

import org.springframework.data.domain.Pageable;
import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.web.dto.requests.ChangePasswordRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.MessageResponse;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;

import java.util.UUID;

/**
 * Controller interface for user management endpoints.
 */
public interface UserController {

    /**
     * Gets the current authenticated user's profile.
     *
     * @return UserResponse with current user's details
     */
    UserResponse getCurrentUser();

    /**
     * Updates the current user's profile.
     *
     * @param request the update request
     * @return UserResponse with updated details
     */
    UserResponse updateCurrentUser(UpdateUserRequest request);

    /**
     * Changes the current user's password.
     *
     * @param request the password change request
     * @return MessageResponse confirming change
     */
    MessageResponse changePassword(ChangePasswordRequest request);

    /**
     * Gets a user by ID (Admin only).
     *
     * @param userId the user's UUID
     * @return UserResponse with user details
     */
    UserResponse getUserById(UUID userId);

    /**
     * Gets all users with pagination (Admin only).
     *
     * @param pageable pagination parameters
     * @return PageResponse with list of users
     */
    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Gets users filtered by role (Admin only).
     *
     * @param role the role to filter by
     * @param pageable pagination parameters
     * @return PageResponse with filtered users
     */
    PageResponse<UserResponse> getUsersByRole(UserRole role, Pageable pageable);

    /**
     * Gets users filtered by status (Admin only).
     *
     * @param status the status to filter by
     * @param pageable pagination parameters
     * @return PageResponse with filtered users
     */
    PageResponse<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable);

    /**
     * Searches users by name or email (Admin only).
     *
     * @param search the search term
     * @param pageable pagination parameters
     * @return PageResponse with matching users
     */
    PageResponse<UserResponse> searchUsers(String search, Pageable pageable);

    /**
     * Updates a user by ID (Admin only).
     *
     * @param userId the user's UUID
     * @param request the update request
     * @return UserResponse with updated details
     */
    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    /**
     * Bans a user (Admin only).
     *
     * @param userId the user's UUID
     * @return UserResponse with updated status
     */
    UserResponse banUser(UUID userId);

    /**
     * Unbans a user (Admin only).
     *
     * @param userId the user's UUID
     * @return UserResponse with updated status
     */
    UserResponse unbanUser(UUID userId);

    /**
     * Deletes a user (Admin only).
     *
     * @param userId the user's UUID
     * @return MessageResponse confirming deletion
     */
    MessageResponse deleteUser(UUID userId);
}
