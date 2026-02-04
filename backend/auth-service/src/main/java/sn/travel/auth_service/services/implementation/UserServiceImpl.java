package sn.travel.auth_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.auth_service.data.entities.User;
import sn.travel.auth_service.data.enums.UserRole;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.data.repositories.RefreshTokenRepository;
import sn.travel.auth_service.data.repositories.UserRepository;
import sn.travel.auth_service.exceptions.InvalidCredentialsException;
import sn.travel.auth_service.exceptions.UserAlreadyExistsException;
import sn.travel.auth_service.exceptions.UserNotFoundException;
import sn.travel.auth_service.services.UserService;
import sn.travel.auth_service.web.dto.requests.ChangePasswordRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;
import sn.travel.auth_service.web.mappers.UserMapper;

import java.util.UUID;

/**
 * Implementation of UserService for user management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.debug("Getting user by ID: {}", userId);
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Getting all users, page: {}", pageable.getPageNumber());
        Page<User> users = userRepository.findAll(pageable);
        return userMapper.toPageResponse(users);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        log.debug("Getting users by role: {}", role);
        Page<User> users = userRepository.findByRole(role, pageable);
        return userMapper.toPageResponse(users);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable) {
        log.debug("Getting users by status: {}", status);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return userMapper.toPageResponse(users);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return userMapper.toPageResponse(users);
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Updating user: {}", userId);
        User user = findUserById(userId);

        // Check if email is being changed and if it's already taken
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new UserAlreadyExistsException(request.email());
            }
        }

        userMapper.updateEntity(request, user);
        user = userRepository.save(user);

        log.info("User updated successfully: {}", userId);
        return userMapper.toResponse(user);
    }

    @Override
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);
        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens to force re-login
        refreshTokenRepository.revokeAllUserTokens(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    @Override
    public UserResponse banUser(UUID userId) {
        log.info("Banning user: {}", userId);
        User user = findUserById(userId);
        user.setStatus(UserStatus.BANNED);
        user = userRepository.save(user);

        // Revoke all tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        log.info("User banned successfully: {}", userId);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse unbanUser(UUID userId) {
        log.info("Unbanning user: {}", userId);
        User user = findUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        log.info("User unbanned successfully: {}", userId);
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        log.info("Deleting user: {}", userId);
        User user = findUserById(userId);

        // Delete all refresh tokens first
        refreshTokenRepository.deleteAllByUserId(userId);

        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }

    @Override
    public UserResponse updatePerformanceScore(UUID managerId, Float score) {
        log.info("Updating performance score for manager: {}", managerId);
        User user = findUserById(managerId);

        if (user.getRole() != UserRole.MANAGER) {
            throw new IllegalArgumentException("Performance score can only be set for managers");
        }

        user.setPerformanceScore(score);
        user = userRepository.save(user);

        log.info("Performance score updated for manager: {}", managerId);
        return userMapper.toResponse(user);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }
}
