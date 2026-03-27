package sn.travel.rec_service.web.controllers.implementation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.travel.rec_service.services.FeedbackService;
import sn.travel.rec_service.web.controllers.FeedbackController;
import sn.travel.rec_service.web.dto.requests.CreateFeedbackRequest;
import sn.travel.rec_service.web.dto.requests.UpdateFeedbackRequest;
import sn.travel.rec_service.web.dto.responses.FeedbackResponse;
import sn.travel.rec_service.web.dto.responses.MessageResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * Implementation du controleur de feedbacks.
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackControllerImpl implements FeedbackController {

    private final FeedbackService feedbackService;

    @Override
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(
            @RequestBody @Valid CreateFeedbackRequest request,
            Principal principal) {
        UUID travelerId = UUID.fromString(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.create(travelerId, request));
    }

    @Override
    @GetMapping("/travel/{travelId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> getByTravel(
            @PathVariable UUID travelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(feedbackService.getByTravel(travelId, page, size));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<PageResponse<FeedbackResponse>> getMyFeedbacks(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID travelerId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(feedbackService.getByTraveler(travelerId, page, size));
    }

    @Override
    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedbackResponse> update(
            @PathVariable Long feedbackId,
            @RequestBody @Valid UpdateFeedbackRequest request,
            Principal principal) {
        UUID travelerId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(feedbackService.update(feedbackId, travelerId, request));
    }

    @Override
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable Long feedbackId,
            Principal principal) {
        UUID travelerId = UUID.fromString(principal.getName());
        String role = extractRole();
        feedbackService.delete(feedbackId, travelerId, role);
        return ResponseEntity.ok(new MessageResponse("Feedback supprime avec succes"));
    }

    private String extractRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("UNKNOWN");
    }
}
