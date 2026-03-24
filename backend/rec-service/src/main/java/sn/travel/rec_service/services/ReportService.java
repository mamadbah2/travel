package sn.travel.rec_service.services;

import sn.travel.rec_service.web.dto.requests.CreateReportRequest;
import sn.travel.rec_service.web.dto.responses.PageResponse;
import sn.travel.rec_service.web.dto.responses.ReportResponse;

import java.util.UUID;

/**
 * Service de gestion des signalements d'utilisateurs.
 */
public interface ReportService {

    ReportResponse create(UUID reporterId, CreateReportRequest request);

    PageResponse<ReportResponse> getAll(int page, int size);

    ReportResponse getById(Long reportId);

    ReportResponse resolve(Long reportId);
}
