package sn.travel.auth_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.auth_service.config.JwtTokenProvider;
import sn.travel.auth_service.data.entities.RefreshToken;
import sn.travel.auth_service.data.entities.User;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.data.repositories.RefreshTokenRepository;
import sn.travel.auth_service.data.repositories.UserRepository;
import sn.travel.auth_service.exceptions.*;
import sn.travel.auth_service.services.AuthService;
import sn.travel.auth_service.web.dto.requests.LoginRequest;
import sn.travel.auth_service.web.dto.requests.RefreshTokenRequest;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.responses.AuthResponse;
import sn.travel.auth_service.web.mappers.UserMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of AuthService for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        return createAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for user: {}", request.email());
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccountDisabledException("Your account has been banned");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getId());
        return createAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");

        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccountDisabledException();
        }

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return createAuthResponse(user);
    }

    @Override
    public void logout(User user) {
        log.info("Logging out user: {}", user.getId());
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus() == UserStatus.ACTIVE,
                true,
                true,
                user.getStatus() != UserStatus.BANNED,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user);

        // Store refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtTokenProvider.getAccessTokenExpiration() * 7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                jwtTokenProvider.getAccessTokenExpiration(),
                userMapper.toResponse(user)
        );
    }
}
