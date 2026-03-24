package sn.travel.rec_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.rec_service.data.enums.ReportStatus;
import sn.travel.rec_service.data.nodes.TravelerNode;
import sn.travel.rec_service.data.relationships.ReportedRelationship;
import sn.travel.rec_service.data.repositories.TravelerNodeRepository;
import sn.travel.rec_service.exceptions.ReportNotFoundException;
import sn.travel.rec_service.services.ReportService;
import sn.travel.rec_service.web.dto.requests.CreateReportRequest;
import sn.travel.rec_service.web.dto.responses.PageResponse;
import sn.travel.rec_service.web.dto.responses.ReportResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation du service de gestion des signalements.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportServiceImpl implements ReportService {

    private final TravelerNodeRepository travelerNodeRepository;

    @Override
    public ReportResponse create(UUID reporterId, CreateReportRequest request) {
        log.info("Creation d'un signalement par {} contre {}", reporterId, request.reportedUserId());

        TravelerNode reporter = travelerNodeRepository.findById(reporterId)
                .orElseGet(() -> TravelerNode.builder()
                        .id(reporterId)
                        .subscriptions(new ArrayList<>())
                        .ratings(new ArrayList<>())
                        .reports(new ArrayList<>())
                        .build());

        TravelerNode reportedUser = travelerNodeRepository.findById(request.reportedUserId())
                .orElseGet(() -> {
                    TravelerNode node = TravelerNode.builder()
                            .id(request.reportedUserId())
                            .subscriptions(new ArrayList<>())
                            .ratings(new ArrayList<>())
                            .reports(new ArrayList<>())
                            .build();
                    return travelerNodeRepository.save(node);
                });

        ReportedRelationship report = ReportedRelationship.builder()
                .reportedUser(reportedUser)
                .reason(request.reason())
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        reporter.getReports().add(report);
        travelerNodeRepository.save(reporter);

        log.info("Signalement cree avec succes par {} contre {}", reporterId, request.reportedUserId());
        return toReportResponse(report, reporterId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getAll(int page, int size) {
        log.debug("Recuperation de tous les signalements (page={}, size={})", page, size);

        List<ReportResponse> allReports = travelerNodeRepository.findAll().stream()
                .flatMap(t -> t.getReports().stream()
                        .map(r -> toReportResponse(r, t.getId())))
                .toList();

        return paginate(allReports, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getById(Long reportId) {
        log.debug("Recuperation du signalement : {}", reportId);

        return travelerNodeRepository.findAll().stream()
                .flatMap(t -> t.getReports().stream()
                        .filter(r -> r.getId().equals(reportId))
                        .map(r -> toReportResponse(r, t.getId())))
                .findFirst()
                .orElseThrow(() -> new ReportNotFoundException(reportId.toString()));
    }

    @Override
    public ReportResponse resolve(Long reportId) {
        log.info("Resolution du signalement : {}", reportId);

        for (TravelerNode traveler : travelerNodeRepository.findAll()) {
            for (ReportedRelationship report : traveler.getReports()) {
                if (report.getId().equals(reportId)) {
                    report.setStatus(ReportStatus.RESOLVED);
                    report.setResolvedAt(LocalDateTime.now());
                    travelerNodeRepository.save(traveler);
                    log.info("Signalement {} resolu avec succes", reportId);
                    return toReportResponse(report, traveler.getId());
                }
            }
        }

        throw new ReportNotFoundException(reportId.toString());
    }

    private ReportResponse toReportResponse(ReportedRelationship report, UUID reporterId) {
        return new ReportResponse(
                report.getId(),
                reporterId,
                report.getReportedUser().getId(),
                report.getReason(),
                report.getStatus().name(),
                report.getCreatedAt(),
                report.getResolvedAt()
        );
    }

    private <T> PageResponse<T> paginate(List<T> items, int page, int size) {
        int totalElements = items.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<T> content = items.subList(fromIndex, toIndex);
        boolean last = page >= totalPages - 1;

        return new PageResponse<>(content, page, size, totalElements, totalPages, last);
    }
}
