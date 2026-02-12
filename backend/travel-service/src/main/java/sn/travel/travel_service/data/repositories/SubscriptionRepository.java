package sn.travel.travel_service.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.travel.travel_service.data.entities.Subscription;
import sn.travel.travel_service.data.enums.SubscriptionStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Subscription entity.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Page<Subscription> findByTravelerId(UUID travelerId, Pageable pageable);

    Page<Subscription> findByTravelId(UUID travelId, Pageable pageable);

    Page<Subscription> findByTravelerIdAndStatus(UUID travelerId, SubscriptionStatus status, Pageable pageable);

    Optional<Subscription> findByTravelerIdAndTravelId(UUID travelerId, UUID travelId);

    boolean existsByTravelerIdAndTravelIdAndStatusNot(UUID travelerId, UUID travelId, SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.travel.id = :travelId AND s.status <> 'CANCELLED'")
    long countActiveSubscriptionsByTravelId(@Param("travelId") UUID travelId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.travel.managerId = :managerId AND s.status = 'CONFIRMED'")
    long countConfirmedSubscriptionsByManagerId(@Param("managerId") UUID managerId);
}
