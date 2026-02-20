package sn.travel.notification_service.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.travel.notification_service.data.entities.Notification;
import sn.travel.notification_service.data.enums.NotificationStatus;
import sn.travel.notification_service.data.enums.NotificationType;

import java.util.UUID;

/**
 * Spring Data JPA repository for Notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByTravelerId(UUID travelerId, Pageable pageable);

    Page<Notification> findByTravelId(UUID travelId, Pageable pageable);

    Page<Notification> findBySubscriptionId(UUID subscriptionId, Pageable pageable);

    Page<Notification> findByType(NotificationType type, Pageable pageable);

    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);
}
