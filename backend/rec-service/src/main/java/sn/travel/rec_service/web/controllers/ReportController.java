package sn.travel.rec_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import sn.travel.rec_service.web.dto.requests.CreateReportRequest;
import sn.travel.rec_service.web.dto.responses.MessageResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;
import sn.travel.rec_service.web.dto.responses.ReportResponse;

import java.security.Principal;

/**
 * Interface du controleur de signalements.
 */
@Tag(name = "Signalements", description = "API de gestion des signalements d'utilisateurs")
public interface ReportController {

    @Operation(summary = "Creer un signalement", description = "Signaler un utilisateur (authentifie)")
    ResponseEntity<ReportResponse> create(@Valid CreateReportRequest request, Principal principal);

    @Operation(summary = "Lister les signalements", description = "Obtenir tous les signalements (ADMIN)")
    ResponseEntity<PageResponse<ReportResponse>> getAll(int page, int size);

    @Operation(summary = "Obtenir un signalement", description = "Obtenir un signalement par ID (ADMIN)")
    ResponseEntity<ReportResponse> getById(Long reportId);

    @Operation(summary = "Resoudre un signalement", description = "Marquer un signalement comme resolu (ADMIN)")
    ResponseEntity<ReportResponse> resolve(Long reportId);
}
