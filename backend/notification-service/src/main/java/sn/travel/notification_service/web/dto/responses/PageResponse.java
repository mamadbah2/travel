package sn.travel.notification_service.web.dto.responses;

import java.util.List;

/**
 * Generic paginated response wrapper.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
