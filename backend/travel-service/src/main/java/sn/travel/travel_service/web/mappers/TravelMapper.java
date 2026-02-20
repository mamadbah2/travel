package sn.travel.travel_service.web.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sn.travel.travel_service.data.entities.Activity;
import sn.travel.travel_service.data.entities.Destination;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.web.dto.requests.ActivityRequest;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.DestinationRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for Travel entity <-> DTO conversions.
 * Replaces MapStruct to avoid Java 25 annotation processing incompatibility.
 */
@Component
public class TravelMapper {

    // ---- Travel mappings ----

    public TravelResponse toResponse(Travel travel) {
        if (travel == null) {
            return null;
        }
        return new TravelResponse(
                travel.getId(),
                travel.getManagerId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getDuration(),
                travel.getPrice(),
                travel.getMaxCapacity(),
                travel.getCurrentBookings(),
                travel.getMaxCapacity() - travel.getCurrentBookings(),
                travel.getStatus(),
                travel.getAccommodationType(),
                travel.getAccommodationName(),
                travel.getTransportationType(),
                travel.getTransportationDetails(),
                toDestinationResponseList(travel.getDestinations()),
                toActivityResponseList(travel.getActivities()),
                travel.getCreatedAt(),
                travel.getUpdatedAt()
        );
    }

    public List<TravelResponse> toResponseList(List<Travel> travels) {
        if (travels == null) {
            return Collections.emptyList();
        }
        return travels.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Travel toEntity(CreateTravelRequest request) {
        if (request == null) {
            return null;
        }
        return Travel.builder()
                .title(request.title())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .duration((int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()))
                .price(request.price())
                .maxCapacity(request.maxCapacity())
                .accommodationType(request.accommodationType())
                .accommodationName(request.accommodationName())
                .transportationType(request.transportationType())
                .transportationDetails(request.transportationDetails())
                .build();
    }

    public void updateEntity(UpdateTravelRequest request, Travel travel) {
        if (request == null || travel == null) {
            return;
        }
        if (request.title() != null) {
            travel.setTitle(request.title());
        }
        if (request.description() != null) {
            travel.setDescription(request.description());
        }
        if (request.startDate() != null) {
            travel.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            travel.setEndDate(request.endDate());
        }
        if (request.startDate() != null || request.endDate() != null) {
            travel.setDuration((int) ChronoUnit.DAYS.between(travel.getStartDate(), travel.getEndDate()));
        }
        if (request.price() != null) {
            travel.setPrice(request.price());
        }
        if (request.maxCapacity() != null) {
            travel.setMaxCapacity(request.maxCapacity());
        }
        if (request.status() != null) {
            travel.setStatus(request.status());
        }
        if (request.accommodationType() != null) {
            travel.setAccommodationType(request.accommodationType());
        }
        if (request.accommodationName() != null) {
            travel.setAccommodationName(request.accommodationName());
        }
        if (request.transportationType() != null) {
            travel.setTransportationType(request.transportationType());
        }
        if (request.transportationDetails() != null) {
            travel.setTransportationDetails(request.transportationDetails());
        }
    }

    // ---- Destination mappings ----

    public DestinationResponse toDestinationResponse(Destination destination) {
        if (destination == null) {
            return null;
        }
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getCountry(),
                destination.getCity(),
                destination.getDescription(),
                destination.getDisplayOrder()
        );
    }

    public List<DestinationResponse> toDestinationResponseList(List<Destination> destinations) {
        if (destinations == null) {
            return Collections.emptyList();
        }
        return destinations.stream()
                .map(this::toDestinationResponse)
                .collect(Collectors.toList());
    }

    public Destination toDestinationEntity(DestinationRequest request) {
        if (request == null) {
            return null;
        }
        return Destination.builder()
                .name(request.name())
                .country(request.country())
                .city(request.city())
                .description(request.description())
                .displayOrder(request.displayOrder())
                .build();
    }

    public List<Destination> toDestinationEntityList(List<DestinationRequest> requests) {
        if (requests == null) {
            return new ArrayList<>();
        }
        return requests.stream()
                .map(this::toDestinationEntity)
                .collect(Collectors.toList());
    }

    // ---- Activity mappings ----

    public ActivityResponse toActivityResponse(Activity activity) {
        if (activity == null) {
            return null;
        }
        return new ActivityResponse(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getLocation(),
                activity.getDisplayOrder()
        );
    }

    public List<ActivityResponse> toActivityResponseList(List<Activity> activities) {
        if (activities == null) {
            return Collections.emptyList();
        }
        return activities.stream()
                .map(this::toActivityResponse)
                .collect(Collectors.toList());
    }

    public Activity toActivityEntity(ActivityRequest request) {
        if (request == null) {
            return null;
        }
        return Activity.builder()
                .name(request.name())
                .description(request.description())
                .location(request.location())
                .displayOrder(request.displayOrder())
                .build();
    }

    public List<Activity> toActivityEntityList(List<ActivityRequest> requests) {
        if (requests == null) {
            return new ArrayList<>();
        }
        return requests.stream()
                .map(this::toActivityEntity)
                .collect(Collectors.toList());
    }

    // ---- Page wrapper ----

    public PageResponse<TravelResponse> toPageResponse(Page<Travel> page) {
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
