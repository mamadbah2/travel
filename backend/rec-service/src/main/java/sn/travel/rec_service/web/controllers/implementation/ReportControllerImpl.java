package sn.travel.rec_service.web.controllers.implementation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.rec_service.services.ReportService;
import sn.travel.rec_service.web.controllers.ReportController;
import sn.travel.rec_service.web.dto.requests.CreateReportRequest;
import sn.travel.rec_service.web.dto.responses.MessageResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;
import sn.travel.rec_service.web.dto.responses.ReportResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * Implementation du controleur de signalements.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

    private final ReportService reportService;

    @Override
    @PostMapping
    public ResponseEntity<ReportResponse> create(
            @RequestBody @Valid CreateReportRequest request,
            Principal principal) {
        UUID reporterId = UUID.fromString(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(reporterId, request));
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<ReportResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reportService.getAll(page, size));
    }

    @Override
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getById(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getById(reportId));
    }

    @Override
    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<ReportResponse> resolve(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.resolve(reportId));
    }
}
