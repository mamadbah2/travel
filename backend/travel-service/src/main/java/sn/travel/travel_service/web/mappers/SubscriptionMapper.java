package sn.travel.travel_service.web.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sn.travel.travel_service.data.entities.Subscription;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for Subscription entity <-> DTO conversions.
 * Replaces MapStruct to avoid Java 25 annotation processing incompatibility.
 */
@Component
public class SubscriptionMapper {

    public SubscriptionResponse toResponse(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getTravelerId(),
                subscription.getTravel() != null ? subscription.getTravel().getId() : null,
                subscription.getTravel() != null ? subscription.getTravel().getTitle() : null,
                subscription.getStatus(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }

    public List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions) {
        if (subscriptions == null) {
            return Collections.emptyList();
        }
        return subscriptions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<SubscriptionResponse> toPageResponse(Page<Subscription> page) {
        if (page == null) {
            return null;
        }
        return new PageResponse<>(
                toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
