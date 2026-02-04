package sn.travel.auth_service.web.controllers;

import sn.travel.auth_service.web.dto.requests.LoginRequest;
import sn.travel.auth_service.web.dto.requests.RefreshTokenRequest;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.responses.AuthResponse;
import sn.travel.auth_service.web.dto.responses.MessageResponse;

/**
 * Controller interface for authentication endpoints.
 */
public interface AuthController {

    /**
     * Registers a new user.
     *
     * @param request the registration request
     * @return AuthResponse with tokens and user info
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user and returns tokens.
     *
     * @param request the login request
     * @return AuthResponse with tokens and user info
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refreshes an access token.
     *
     * @param request the refresh token request
     * @return AuthResponse with new tokens
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logs out the current user.
     *
     * @return MessageResponse confirming logout
     */
    MessageResponse logout();
}
