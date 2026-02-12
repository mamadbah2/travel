package sn.travel.travel_service.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sn.travel.travel_service.data.enums.AccommodationType;
import sn.travel.travel_service.data.enums.TransportationType;
import sn.travel.travel_service.data.enums.TravelStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core aggregate entity representing a travel offer.
 * Managed by Travel Managers, browsed and subscribed to by Travelers.
 */
@Entity
@Table(name = "travels", indexes = {
        @Index(name = "idx_travel_manager", columnList = "manager_id"),
        @Index(name = "idx_travel_status", columnList = "status"),
        @Index(name = "idx_travel_start_date", columnList = "start_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Travel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_bookings", nullable = false)
    @Builder.Default
    private Integer currentBookings = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TravelStatus status = TravelStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_type", length = 30)
    private AccommodationType accommodationType;

    @Column(name = "accommodation_name", length = 255)
    private String accommodationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", length = 30)
    private TransportationType transportationType;

    @Column(name = "transportation_details", length = 500)
    private String transportationDetails;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Destination> destinations = new ArrayList<>();

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Subscription> subscriptions = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Helper: Check if the travel has available capacity.
     */
    public boolean hasAvailableCapacity() {
        return currentBookings < maxCapacity;
    }

    /**
     * Helper: Increment booking count (used with optimistic locking).
     */
    public void incrementBookings() {
        this.currentBookings++;
    }

    /**
     * Helper: Decrement booking count.
     */
    public void decrementBookings() {
        if (this.currentBookings > 0) {
            this.currentBookings--;
        }
    }
}
