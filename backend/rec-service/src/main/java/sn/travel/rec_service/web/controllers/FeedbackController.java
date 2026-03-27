package sn.travel.rec_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import sn.travel.rec_service.web.dto.requests.CreateFeedbackRequest;
import sn.travel.rec_service.web.dto.requests.UpdateFeedbackRequest;
import sn.travel.rec_service.web.dto.responses.FeedbackResponse;
import sn.travel.rec_service.web.dto.responses.MessageResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * Interface du controleur de feedbacks (notations).
 */
@Tag(name = "Feedbacks", description = "API de gestion des notations de voyages")
public interface FeedbackController {

    @Operation(summary = "Creer un feedback", description = "Donner une note et un commentaire sur un voyage (TRAVELER)")
    ResponseEntity<FeedbackResponse> create(@Valid CreateFeedbackRequest request, Principal principal);

    @Operation(summary = "Feedbacks par voyage", description = "Lister les feedbacks d'un voyage (public)")
    ResponseEntity<PageResponse<FeedbackResponse>> getByTravel(UUID travelId, int page, int size);

    @Operation(summary = "Mes feedbacks", description = "Lister les feedbacks du voyageur connecte")
    ResponseEntity<PageResponse<FeedbackResponse>> getMyFeedbacks(Principal principal, int page, int size);

    @Operation(summary = "Modifier un feedback", description = "Modifier sa note ou son commentaire (TRAVELER)")
    ResponseEntity<FeedbackResponse> update(Long feedbackId, @Valid UpdateFeedbackRequest request, Principal principal);

    @Operation(summary = "Supprimer un feedback", description = "Supprimer un feedback (proprietaire ou ADMIN)")
    ResponseEntity<MessageResponse> delete(Long feedbackId, Principal principal);
}
