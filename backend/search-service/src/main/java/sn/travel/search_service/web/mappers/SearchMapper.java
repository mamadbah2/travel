package sn.travel.search_service.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import sn.travel.search_service.data.documents.TravelDocument;
import sn.travel.search_service.data.records.TravelCreatedEvent;
import sn.travel.search_service.data.records.TravelUpdatedEvent;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;

import java.util.UUID;

/**
 * MapStruct mapper for search-service data transformations.
 * Handles:
 * - Event → TravelDocument (indexing)
 * - TravelDocument → SearchResultResponse (API response)
 */
@Mapper(componentModel = "spring")
public interface SearchMapper {

    // ---- Event → Document mappings ----

    @Mapping(target = "id", source = "travelId", qualifiedByName = "uuidToString")
    @Mapping(target = "managerId", source = "managerId", qualifiedByName = "uuidToString")
    TravelDocument toDocument(TravelCreatedEvent event);

    @Mapping(target = "id", source = "travelId", qualifiedByName = "uuidToString")
    @Mapping(target = "managerId", source = "managerId", qualifiedByName = "uuidToString")
    TravelDocument toDocument(TravelUpdatedEvent event);

    // ---- Nested event → document mappings ----

    TravelDocument.DestinationDoc toDestinationDoc(TravelCreatedEvent.DestinationData data);
    TravelDocument.ActivityDoc toActivityDoc(TravelCreatedEvent.ActivityData data);
    TravelDocument.DestinationDoc toDestinationDoc(TravelUpdatedEvent.DestinationData data);
    TravelDocument.ActivityDoc toActivityDoc(TravelUpdatedEvent.ActivityData data);

    // ---- Document → Response mappings ----

    @Mapping(target = "availableSpots", expression = "java(document.getMaxCapacity() != null && document.getCurrentBookings() != null ? document.getMaxCapacity() - document.getCurrentBookings() : null)")
    SearchResultResponse toResponse(TravelDocument document);

    SearchResultResponse.DestinationInfo toDestinationInfo(TravelDocument.DestinationDoc doc);
    SearchResultResponse.ActivityInfo toActivityInfo(TravelDocument.ActivityDoc doc);

    // ---- Type converters ----

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
