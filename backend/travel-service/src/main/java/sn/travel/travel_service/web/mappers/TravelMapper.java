package sn.travel.travel_service.web.mappers;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import sn.travel.travel_service.data.entities.Activity;
import sn.travel.travel_service.data.entities.Destination;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.web.dto.requests.ActivityRequest;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.DestinationRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * MapStruct mapper for Travel entity to DTO conversions.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TravelMapper {

    // ---- Travel mappings ----

    @Mapping(target = "availableSpots", expression = "java(travel.getMaxCapacity() - travel.getCurrentBookings())")
    TravelResponse toResponse(Travel travel);

    List<TravelResponse> toResponseList(List<Travel> travels);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "managerId", ignore = true)
    @Mapping(target = "currentBookings", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "duration", expression = "java((int) java.time.temporal.ChronoUnit.DAYS.between(request.startDate(), request.endDate()))")
    @Mapping(target = "destinations", ignore = true)
    @Mapping(target = "activities", ignore = true)
    Travel toEntity(CreateTravelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "managerId", ignore = true)
    @Mapping(target = "currentBookings", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "destinations", ignore = true)
    @Mapping(target = "activities", ignore = true)
    void updateEntity(UpdateTravelRequest request, @MappingTarget Travel travel);

    // ---- Destination mappings ----

    DestinationResponse toDestinationResponse(Destination destination);

    List<DestinationResponse> toDestinationResponseList(List<Destination> destinations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "travel", ignore = true)
    Destination toDestinationEntity(DestinationRequest request);

    List<Destination> toDestinationEntityList(List<DestinationRequest> requests);

    // ---- Activity mappings ----

    ActivityResponse toActivityResponse(Activity activity);

    List<ActivityResponse> toActivityResponseList(List<Activity> activities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "travel", ignore = true)
    Activity toActivityEntity(ActivityRequest request);

    List<Activity> toActivityEntityList(List<ActivityRequest> requests);

    // ---- Page wrapper ----

    default PageResponse<TravelResponse> toPageResponse(Page<Travel> page) {
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
