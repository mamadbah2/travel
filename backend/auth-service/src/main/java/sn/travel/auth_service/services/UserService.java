package sn.travel.auth_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.web.dto.requests.ChangePasswordRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;

import java.util.UUID;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Gets a user by their ID.
     *
     * @param userId the user's UUID
     * @return UserResponse containing user details
     */
    UserResponse getUserById(UUID userId);

    /**
     * Gets a user by their email.
     *
     * @param email the user's email
     * @return UserResponse containing user details
     */
    UserResponse getUserByEmail(String email);

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination parameters
     * @return PageResponse containing list of users
     */
    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Gets users by role with pagination.
     *
     * @param role the user role to filter by
     * @param pageable pagination parameters
     * @return PageResponse containing filtered list of users
     */
    PageResponse<UserResponse> getUsersByRole(UserRole role, Pageable pageable);

    /**
     * Gets users by status with pagination.
     *
     * @param status the user status to filter by
     * @param pageable pagination parameters
     * @return PageResponse containing filtered list of users
     */
    PageResponse<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable);

    /**
     * Searches users by name or email.
     *
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return PageResponse containing matching users
     */
    PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Updates a user's information.
     *
     * @param userId the user's UUID
     * @param request the update request
     * @return UserResponse with updated user details
     */
    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    /**
     * Changes a user's password.
     *
     * @param userId the user's UUID
     * @param request the password change request
     */
    void changePassword(UUID userId, ChangePasswordRequest request);

    /**
     * Bans a user account.
     *
     * @param userId the user's UUID
     * @return UserResponse with updated status
     */
    UserResponse banUser(UUID userId);

    /**
     * Unbans a user account.
     *
     * @param userId the user's UUID
     * @return UserResponse with updated status
     */
    UserResponse unbanUser(UUID userId);

    /**
     * Deletes a user account.
     *
     * @param userId the user's UUID
     */
    void deleteUser(UUID userId);

    /**
     * Updates a manager's performance score.
     *
     * @param managerId the manager's UUID
     * @param score the new performance score
     * @return UserResponse with updated score
     */
    UserResponse updatePerformanceScore(UUID managerId, Float score);
}
