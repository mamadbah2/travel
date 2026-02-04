package sn.travel.auth_service.web.mappers;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import sn.travel.auth_service.data.entities.User;
import sn.travel.auth_service.web.dto.requests.RegisterRequest;
import sn.travel.auth_service.web.dto.requests.UpdateUserRequest;
import sn.travel.auth_service.web.dto.responses.PageResponse;
import sn.travel.auth_service.web.dto.responses.UserResponse;

import java.util.List;

/**
 * MapStruct mapper for User entity to DTO conversions.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Converts a User entity to UserResponse DTO.
     */
    UserResponse toResponse(User user);

    /**
     * Converts a list of User entities to a list of UserResponse DTOs.
     */
    List<UserResponse> toResponseList(List<User> users);

    /**
     * Converts a RegisterRequest DTO to a User entity.
     * Password should be encoded separately in the service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Updates a User entity with data from UpdateUserRequest DTO.
     * Only non-null fields are updated.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    /**
     * Converts a Spring Data Page to a PageResponse.
     */
    default PageResponse<UserResponse> toPageResponse(Page<User> page) {
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
