package sn.travel.search_service.data.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch document representing a searchable Travel offer.
 * Indexed via RabbitMQ events from the travel-service (CQRS pattern).
 */
@Document(indexName = "travels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String managerId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate startDate;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate endDate;

    @Field(type = FieldType.Integer)
    private Integer duration;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Integer)
    private Integer maxCapacity;

    @Field(type = FieldType.Integer)
    private Integer currentBookings;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String accommodationType;

    @Field(type = FieldType.Text)
    private String accommodationName;

    @Field(type = FieldType.Keyword)
    private String transportationType;

    @Field(type = FieldType.Text)
    private String transportationDetails;

    @Field(type = FieldType.Object)
    private List<DestinationDoc> destinations;

    @Field(type = FieldType.Object)
    private List<ActivityDoc> activities;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    // ---- Nested object classes ----

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DestinationDoc {

        @Field(type = FieldType.Text)
        private String name;

        @Field(type = FieldType.Keyword)
        private String country;

        @Field(type = FieldType.Keyword)
        private String city;

        @Field(type = FieldType.Text)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivityDoc {

        @Field(type = FieldType.Text)
        private String name;

        @Field(type = FieldType.Text)
        private String description;

        @Field(type = FieldType.Text)
        private String location;
    }
}
