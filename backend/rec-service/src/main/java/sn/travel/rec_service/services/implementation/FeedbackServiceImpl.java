package sn.travel.rec_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.rec_service.data.nodes.TravelNode;
import sn.travel.rec_service.data.nodes.TravelerNode;
import sn.travel.rec_service.data.relationships.RatedRelationship;
import sn.travel.rec_service.data.repositories.TravelNodeRepository;
import sn.travel.rec_service.data.repositories.TravelerNodeRepository;
import sn.travel.rec_service.exceptions.DuplicateFeedbackException;
import sn.travel.rec_service.exceptions.FeedbackNotFoundException;
import sn.travel.rec_service.exceptions.UnauthorizedFeedbackException;
import sn.travel.rec_service.services.FeedbackService;
import sn.travel.rec_service.web.dto.requests.CreateFeedbackRequest;
import sn.travel.rec_service.web.dto.requests.UpdateFeedbackRequest;
import sn.travel.rec_service.web.dto.responses.FeedbackResponse;
import sn.travel.rec_service.web.dto.responses.PageResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation du service de gestion des feedbacks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final TravelerNodeRepository travelerNodeRepository;
    private final TravelNodeRepository travelNodeRepository;

    @Override
    public FeedbackResponse create(UUID travelerId, CreateFeedbackRequest request) {
        log.info("Creation d'un feedback par le voyageur {} pour le voyage {}", travelerId, request.travelId());

        TravelerNode traveler = travelerNodeRepository.findById(travelerId)
                .orElseGet(() -> TravelerNode.builder()
                        .id(travelerId)
                        .subscriptions(new ArrayList<>())
                        .ratings(new ArrayList<>())
                        .reports(new ArrayList<>())
                        .build());

        // Verifier qu'il n'a pas deja donne un feedback pour ce voyage
        boolean alreadyRated = traveler.getRatings().stream()
                .anyMatch(r -> r.getTravel().getId().equals(request.travelId()));
        if (alreadyRated) {
            throw new DuplicateFeedbackException();
        }

        TravelNode travel = travelNodeRepository.findById(request.travelId())
                .orElseThrow(() -> new FeedbackNotFoundException(request.travelId().toString()));

        RatedRelationship rating = RatedRelationship.builder()
                .travel(travel)
                .rating(request.rating())
                .comment(request.comment())
                .createdAt(LocalDateTime.now())
                .build();

        traveler.getRatings().add(rating);
        travelerNodeRepository.save(traveler);

        log.info("Feedback cree avec succes pour le voyage {}", request.travelId());
        return toFeedbackResponse(rating, travelerId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FeedbackResponse> getByTravel(UUID travelId, int page, int size) {
        log.debug("Recuperation des feedbacks pour le voyage : {}", travelId);

        List<TravelerNode> travelers = travelerNodeRepository.findAll();
        List<FeedbackResponse> allFeedbacks = travelers.stream()
                .flatMap(t -> t.getRatings().stream()
                        .filter(r -> r.getTravel().getId().equals(travelId))
                        .map(r -> toFeedbackResponse(r, t.getId())))
                .toList();

        return paginate(allFeedbacks, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FeedbackResponse> getByTraveler(UUID travelerId, int page, int size) {
        log.debug("Recuperation des feedbacks du voyageur : {}", travelerId);

        TravelerNode traveler = travelerNodeRepository.findById(travelerId).orElse(null);
        if (traveler == null) {
            return new PageResponse<>(List.of(), page, size, 0, 0, true);
        }

        List<FeedbackResponse> feedbacks = traveler.getRatings().stream()
                .map(r -> toFeedbackResponse(r, travelerId))
                .toList();

        return paginate(feedbacks, page, size);
    }

    @Override
    public FeedbackResponse update(Long feedbackId, UUID travelerId, UpdateFeedbackRequest request) {
        log.info("Mise a jour du feedback {} par le voyageur {}", feedbackId, travelerId);

        TravelerNode traveler = travelerNodeRepository.findById(travelerId)
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId.toString()));

        RatedRelationship rating = traveler.getRatings().stream()
                .filter(r -> r.getId().equals(feedbackId))
                .findFirst()
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId.toString()));

        if (request.rating() != null) {
            rating.setRating(request.rating());
        }
        if (request.comment() != null) {
            rating.setComment(request.comment());
        }

        travelerNodeRepository.save(traveler);
        log.info("Feedback {} mis a jour avec succes", feedbackId);
        return toFeedbackResponse(rating, travelerId);
    }

    @Override
    public void delete(Long feedbackId, UUID travelerId, String role) {
        log.info("Suppression du feedback {} par l'utilisateur {} (role={})", feedbackId, travelerId, role);

        TravelerNode traveler;
        if ("ADMIN".equals(role)) {
            // Un admin peut supprimer n'importe quel feedback
            traveler = travelerNodeRepository.findAll().stream()
                    .filter(t -> t.getRatings().stream().anyMatch(r -> r.getId().equals(feedbackId)))
                    .findFirst()
                    .orElseThrow(() -> new FeedbackNotFoundException(feedbackId.toString()));
        } else {
            traveler = travelerNodeRepository.findById(travelerId)
                    .orElseThrow(() -> new FeedbackNotFoundException(feedbackId.toString()));

            boolean owns = traveler.getRatings().stream()
                    .anyMatch(r -> r.getId().equals(feedbackId));
            if (!owns) {
                throw new UnauthorizedFeedbackException();
            }
        }

        traveler.getRatings().removeIf(r -> r.getId().equals(feedbackId));
        travelerNodeRepository.save(traveler);
        log.info("Feedback {} supprime avec succes", feedbackId);
    }

    private FeedbackResponse toFeedbackResponse(RatedRelationship rating, UUID travelerId) {
        return new FeedbackResponse(
                rating.getId(),
                travelerId,
                rating.getTravel().getId(),
                rating.getTravel().getTitle(),
                rating.getRating(),
                rating.getComment(),
                rating.getCreatedAt()
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
