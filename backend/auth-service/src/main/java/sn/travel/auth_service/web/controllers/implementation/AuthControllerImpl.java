package sn.travel.auth_service.web.controllers.implementation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sn.travel.auth_service.services.AuthService;
import sn.travel.auth_service.web.controllers.AuthController;
import sn.travel.auth_service.web.dto.requests.LoginRequest;
import sn.travel.auth_service.web.dto.requests.RefreshTokenRequest;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.responses.AuthResponse;
import sn.travel.auth_service.web.dto.responses.MessageResponse;

/**
 * REST Controller implementation for authentication endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and token management endpoints")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT tokens")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Override
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses a refresh token to obtain new JWT tokens")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @Override
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates all refresh tokens for the current user")
    public MessageResponse logout() {
        authService.logout(authService.getCurrentUser());
        return new MessageResponse("Successfully logged out");
    }
}
