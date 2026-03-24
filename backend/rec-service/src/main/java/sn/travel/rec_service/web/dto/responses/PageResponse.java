package sn.travel.rec_service.web.dto.responses;

import java.util.List;

/**
 * Reponse paginee generique.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
