package sn.travel.rec_service.services;

import sn.travel.rec_service.web.dto.requests.CreateFeedbackRequest;
import sn.travel.rec_service.web.dto.requests.UpdateFeedbackRequest;
import sn.travel.rec_service.web.dto.responses.FeedbackResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;

import java.util.UUID;

/**
 * Service de gestion des feedbacks (notations) sur les voyages.
 */
public interface FeedbackService {

    FeedbackResponse create(UUID travelerId, CreateFeedbackRequest request);

    PageResponse<FeedbackResponse> getByTravel(UUID travelId, int page, int size);

    PageResponse<FeedbackResponse> getByTraveler(UUID travelerId, int page, int size);

    FeedbackResponse update(Long feedbackId, UUID travelerId, UpdateFeedbackRequest request);

    void delete(Long feedbackId, UUID travelerId, String role);
}
