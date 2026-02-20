package sn.travel.search_service.web.mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sn.travel.search_service.data.documents.TravelDocument;
import sn.travel.search_service.data.records.TravelCreatedEvent;
import sn.travel.search_service.data.records.TravelUpdatedEvent;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-20T11:30:57+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Red Hat, Inc.)"
)
@Component
public class SearchMapperImpl implements SearchMapper {

    @Override
    public TravelDocument toDocument(TravelCreatedEvent event) {
        if ( event == null ) {
            return null;
        }

        TravelDocument.TravelDocumentBuilder travelDocument = TravelDocument.builder();

        travelDocument.id( uuidToString( event.travelId() ) );
        travelDocument.managerId( uuidToString( event.managerId() ) );
        travelDocument.title( event.title() );
        travelDocument.description( event.description() );
        travelDocument.startDate( event.startDate() );
        travelDocument.endDate( event.endDate() );
        travelDocument.duration( event.duration() );
        travelDocument.price( event.price() );
        travelDocument.maxCapacity( event.maxCapacity() );
        travelDocument.currentBookings( event.currentBookings() );
        travelDocument.status( event.status() );
        travelDocument.accommodationType( event.accommodationType() );
        travelDocument.accommodationName( event.accommodationName() );
        travelDocument.transportationType( event.transportationType() );
        travelDocument.transportationDetails( event.transportationDetails() );
        travelDocument.destinations( destinationDataListToDestinationDocList( event.destinations() ) );
        travelDocument.activities( activityDataListToActivityDocList( event.activities() ) );
        travelDocument.createdAt( event.createdAt() );
        travelDocument.updatedAt( event.updatedAt() );

        return travelDocument.build();
    }

    @Override
    public TravelDocument toDocument(TravelUpdatedEvent event) {
        if ( event == null ) {
            return null;
        }

        TravelDocument.TravelDocumentBuilder travelDocument = TravelDocument.builder();

        travelDocument.id( uuidToString( event.travelId() ) );
        travelDocument.managerId( uuidToString( event.managerId() ) );
        travelDocument.title( event.title() );
        travelDocument.description( event.description() );
        travelDocument.startDate( event.startDate() );
        travelDocument.endDate( event.endDate() );
        travelDocument.duration( event.duration() );
        travelDocument.price( event.price() );
        travelDocument.maxCapacity( event.maxCapacity() );
        travelDocument.currentBookings( event.currentBookings() );
        travelDocument.status( event.status() );
        travelDocument.accommodationType( event.accommodationType() );
        travelDocument.accommodationName( event.accommodationName() );
        travelDocument.transportationType( event.transportationType() );
        travelDocument.transportationDetails( event.transportationDetails() );
        travelDocument.destinations( destinationDataListToDestinationDocList1( event.destinations() ) );
        travelDocument.activities( activityDataListToActivityDocList1( event.activities() ) );
        travelDocument.createdAt( event.createdAt() );
        travelDocument.updatedAt( event.updatedAt() );

        return travelDocument.build();
    }

    @Override
    public TravelDocument.DestinationDoc toDestinationDoc(TravelCreatedEvent.DestinationData data) {
        if ( data == null ) {
            return null;
        }

        TravelDocument.DestinationDoc.DestinationDocBuilder destinationDoc = TravelDocument.DestinationDoc.builder();

        destinationDoc.name( data.name() );
        destinationDoc.country( data.country() );
        destinationDoc.city( data.city() );
        destinationDoc.description( data.description() );

        return destinationDoc.build();
    }

    @Override
    public TravelDocument.ActivityDoc toActivityDoc(TravelCreatedEvent.ActivityData data) {
        if ( data == null ) {
            return null;
        }

        TravelDocument.ActivityDoc.ActivityDocBuilder activityDoc = TravelDocument.ActivityDoc.builder();

        activityDoc.name( data.name() );
        activityDoc.description( data.description() );
        activityDoc.location( data.location() );

        return activityDoc.build();
    }

    @Override
    public TravelDocument.DestinationDoc toDestinationDoc(TravelUpdatedEvent.DestinationData data) {
        if ( data == null ) {
            return null;
        }

        TravelDocument.DestinationDoc.DestinationDocBuilder destinationDoc = TravelDocument.DestinationDoc.builder();

        destinationDoc.name( data.name() );
        destinationDoc.country( data.country() );
        destinationDoc.city( data.city() );
        destinationDoc.description( data.description() );

        return destinationDoc.build();
    }

    @Override
    public TravelDocument.ActivityDoc toActivityDoc(TravelUpdatedEvent.ActivityData data) {
        if ( data == null ) {
            return null;
        }

        TravelDocument.ActivityDoc.ActivityDocBuilder activityDoc = TravelDocument.ActivityDoc.builder();

        activityDoc.name( data.name() );
        activityDoc.description( data.description() );
        activityDoc.location( data.location() );

        return activityDoc.build();
    }

    @Override
    public SearchResultResponse toResponse(TravelDocument document) {
        if ( document == null ) {
            return null;
        }

        String id = null;
        String managerId = null;
        String title = null;
        String description = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        Integer duration = null;
        Double price = null;
        Integer maxCapacity = null;
        Integer currentBookings = null;
        String status = null;
        String accommodationType = null;
        String accommodationName = null;
        String transportationType = null;
        String transportationDetails = null;
        List<SearchResultResponse.DestinationInfo> destinations = null;
        List<SearchResultResponse.ActivityInfo> activities = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = document.getId();
        managerId = document.getManagerId();
        title = document.getTitle();
        description = document.getDescription();
        startDate = document.getStartDate();
        endDate = document.getEndDate();
        duration = document.getDuration();
        price = document.getPrice();
        maxCapacity = document.getMaxCapacity();
        currentBookings = document.getCurrentBookings();
        status = document.getStatus();
        accommodationType = document.getAccommodationType();
        accommodationName = document.getAccommodationName();
        transportationType = document.getTransportationType();
        transportationDetails = document.getTransportationDetails();
        destinations = destinationDocListToDestinationInfoList( document.getDestinations() );
        activities = activityDocListToActivityInfoList( document.getActivities() );
        createdAt = document.getCreatedAt();
        updatedAt = document.getUpdatedAt();

        Integer availableSpots = document.getMaxCapacity() != null && document.getCurrentBookings() != null ? document.getMaxCapacity() - document.getCurrentBookings() : null;

        SearchResultResponse searchResultResponse = new SearchResultResponse( id, managerId, title, description, startDate, endDate, duration, price, maxCapacity, currentBookings, availableSpots, status, accommodationType, accommodationName, transportationType, transportationDetails, destinations, activities, createdAt, updatedAt );

        return searchResultResponse;
    }

    @Override
    public SearchResultResponse.DestinationInfo toDestinationInfo(TravelDocument.DestinationDoc doc) {
        if ( doc == null ) {
            return null;
        }

        String name = null;
        String country = null;
        String city = null;
        String description = null;

        name = doc.getName();
        country = doc.getCountry();
        city = doc.getCity();
        description = doc.getDescription();

        SearchResultResponse.DestinationInfo destinationInfo = new SearchResultResponse.DestinationInfo( name, country, city, description );

        return destinationInfo;
    }

    @Override
    public SearchResultResponse.ActivityInfo toActivityInfo(TravelDocument.ActivityDoc doc) {
        if ( doc == null ) {
            return null;
        }

        String name = null;
        String description = null;
        String location = null;

        name = doc.getName();
        description = doc.getDescription();
        location = doc.getLocation();

        SearchResultResponse.ActivityInfo activityInfo = new SearchResultResponse.ActivityInfo( name, description, location );

        return activityInfo;
    }

    protected List<TravelDocument.DestinationDoc> destinationDataListToDestinationDocList(List<TravelCreatedEvent.DestinationData> list) {
        if ( list == null ) {
            return null;
        }

        List<TravelDocument.DestinationDoc> list1 = new ArrayList<TravelDocument.DestinationDoc>( list.size() );
        for ( TravelCreatedEvent.DestinationData destinationData : list ) {
            list1.add( toDestinationDoc( destinationData ) );
        }

        return list1;
    }

    protected List<TravelDocument.ActivityDoc> activityDataListToActivityDocList(List<TravelCreatedEvent.ActivityData> list) {
        if ( list == null ) {
            return null;
        }

        List<TravelDocument.ActivityDoc> list1 = new ArrayList<TravelDocument.ActivityDoc>( list.size() );
        for ( TravelCreatedEvent.ActivityData activityData : list ) {
            list1.add( toActivityDoc( activityData ) );
        }

        return list1;
    }

    protected List<TravelDocument.DestinationDoc> destinationDataListToDestinationDocList1(List<TravelUpdatedEvent.DestinationData> list) {
        if ( list == null ) {
            return null;
        }

        List<TravelDocument.DestinationDoc> list1 = new ArrayList<TravelDocument.DestinationDoc>( list.size() );
        for ( TravelUpdatedEvent.DestinationData destinationData : list ) {
            list1.add( toDestinationDoc( destinationData ) );
        }

        return list1;
    }

    protected List<TravelDocument.ActivityDoc> activityDataListToActivityDocList1(List<TravelUpdatedEvent.ActivityData> list) {
        if ( list == null ) {
            return null;
        }

        List<TravelDocument.ActivityDoc> list1 = new ArrayList<TravelDocument.ActivityDoc>( list.size() );
        for ( TravelUpdatedEvent.ActivityData activityData : list ) {
            list1.add( toActivityDoc( activityData ) );
        }

        return list1;
    }

    protected List<SearchResultResponse.DestinationInfo> destinationDocListToDestinationInfoList(List<TravelDocument.DestinationDoc> list) {
        if ( list == null ) {
            return null;
        }

        List<SearchResultResponse.DestinationInfo> list1 = new ArrayList<SearchResultResponse.DestinationInfo>( list.size() );
        for ( TravelDocument.DestinationDoc destinationDoc : list ) {
            list1.add( toDestinationInfo( destinationDoc ) );
        }

        return list1;
    }

    protected List<SearchResultResponse.ActivityInfo> activityDocListToActivityInfoList(List<TravelDocument.ActivityDoc> list) {
        if ( list == null ) {
            return null;
        }

        List<SearchResultResponse.ActivityInfo> list1 = new ArrayList<SearchResultResponse.ActivityInfo>( list.size() );
        for ( TravelDocument.ActivityDoc activityDoc : list ) {
            list1.add( toActivityInfo( activityDoc ) );
        }

        return list1;
    }
}
