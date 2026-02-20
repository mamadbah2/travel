package sn.travel.payment_service.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.travel.payment_service.data.entities.Payment;
import sn.travel.payment_service.data.enums.PaymentStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Payment entity operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findBySubscriptionId(UUID subscriptionId);

    boolean existsBySubscriptionId(UUID subscriptionId);

    Page<Payment> findByTravelerId(UUID travelerId, Pageable pageable);

    Page<Payment> findByTravelId(UUID travelId, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
}
