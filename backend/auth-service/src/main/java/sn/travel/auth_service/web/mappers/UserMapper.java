package sn.travel.auth_service.web.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sn.travel.auth_service.data.entities.User;
import sn.travel.auth_service.data.enums.UserStatus;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for User entity <-> DTO conversions.
 * Replaces MapStruct to avoid Java 25 annotation processing incompatibility.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getPerformanceScore(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void updateEntity(UpdateUserRequest request, User user) {
        if (request == null || user == null) {
            return;
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        if (request.performanceScore() != null) {
            user.setPerformanceScore(request.performanceScore());
        }
    }

    public PageResponse<UserResponse> toPageResponse(Page<User> page) {
        if (page == null) {
            return null;
        }
        return new PageResponse<>(
                toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
