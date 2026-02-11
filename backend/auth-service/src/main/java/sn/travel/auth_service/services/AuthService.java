package sn.travel.auth_service.services;

import sn.travel.auth_service.data.entities.User;
import sn.travel.auth_service.web.dto.requests.LoginRequest;
import sn.travel.auth_service.web.dto.requests.RefreshTokenRequest;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.responses.AuthResponse;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return AuthResponse with JWT tokens and user information
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     *
     * @param request the login request containing credentials
     * @return AuthResponse with JWT tokens and user information
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param request the refresh token request
     * @return AuthResponse with new JWT tokens
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logs out a user by invalidating their refresh tokens.
     *
     * @param user the user to logout
     */
    void logout(User user);

    /**
     * Gets the currently authenticated user from the security context.
     *
     * @return the current user
     */
    User getCurrentUser();
}
