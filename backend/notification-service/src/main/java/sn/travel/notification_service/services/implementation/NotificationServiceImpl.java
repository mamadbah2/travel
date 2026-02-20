package sn.travel.notification_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.notification_service.data.entities.Notification;
import sn.travel.notification_service.data.enums.NotificationStatus;
import sn.travel.notification_service.data.enums.NotificationType;
import sn.travel.notification_service.data.records.PaymentCompletedEvent;
import sn.travel.notification_service.data.records.SubscriptionCreatedEvent;
import sn.travel.notification_service.data.repositories.NotificationRepository;
import sn.travel.notification_service.exceptions.NotificationNotFoundException;
import sn.travel.notification_service.services.EmailService;
import sn.travel.notification_service.services.NotificationService;
import sn.travel.notification_service.web.dto.responses.NotificationResponse;
import sn.travel.notification_service.web.dto.responses.PageResponse;
import sn.travel.notification_service.web.mappers.NotificationMapper;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of NotificationService.
 * Processes RabbitMQ events, sends emails via EmailService, and persists notification records.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    /**
     * Temporary: In a real system, we'd call the auth-service to get the traveler's email.
     * For now, we generate a deterministic email from the travelerId.
     */
    private String resolveTravelerEmail(UUID travelerId) {
        // TODO: Replace with REST call to auth-service (GET /api/v1/users/{id}) or cache
        return "traveler-" + travelerId.toString().substring(0, 8) + "@travel.sn";
    }

    @Override
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Processing subscription notification: subscriptionId={}, travelTitle='{}'",
                event.subscriptionId(), event.travelTitle());

        String recipientEmail = resolveTravelerEmail(event.travelerId());
        String subject = "✈️ Booking Received - " + event.travelTitle();

        // Build template variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("travelTitle", event.travelTitle());
        variables.put("subscriptionId", event.subscriptionId().toString());
        variables.put("amount", formatAmount(event.amount(), event.currency()));
        variables.put("currency", event.currency() != null ? event.currency() : "XOF");

        // Create notification record
        Notification notification = Notification.builder()
                .travelerId(event.travelerId())
                .travelId(event.travelId())
                .subscriptionId(event.subscriptionId())
                .recipientEmail(recipientEmail)
                .subject(subject)
                .type(NotificationType.SUBSCRIPTION_CREATED)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            emailService.sendHtmlEmail(recipientEmail, subject, "subscription-created", variables);
            notification.setStatus(NotificationStatus.SENT);
            notification.setBody("Booking received email sent for travel: " + event.travelTitle());
            log.info("Subscription notification sent to {} for subscription {}", recipientEmail, event.subscriptionId());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("Failed to send subscription notification for subscription {}: {}",
                    event.subscriptionId(), e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Override
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Processing payment notification: subscriptionId={}, status='{}'",
                event.subscriptionId(), event.status());

        String recipientEmail = resolveTravelerEmail(event.travelerId());
        boolean isSuccess = "SUCCESS".equalsIgnoreCase(event.status());

        String subject;
        String templateName;
        NotificationType type;
        Map<String, Object> variables = new HashMap<>();
        variables.put("subscriptionId", event.subscriptionId().toString());
        variables.put("transactionId", event.transactionId());

        if (isSuccess) {
            subject = "✅ Payment Successful - Your Trip is Confirmed!";
            templateName = "payment-success";
            type = NotificationType.PAYMENT_SUCCESS;
            variables.put("status", "SUCCESS");
        } else {
            subject = "❌ Payment Failed - Action Required";
            templateName = "payment-failed";
            type = NotificationType.PAYMENT_FAILED;
            variables.put("status", "FAILED");
            variables.put("failureReason", event.failureReason() != null ? event.failureReason() : "Unknown error");
        }

        Notification notification = Notification.builder()
                .travelerId(event.travelerId())
                .travelId(event.travelId())
                .subscriptionId(event.subscriptionId())
                .recipientEmail(recipientEmail)
                .subject(subject)
                .type(type)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            emailService.sendHtmlEmail(recipientEmail, subject, templateName, variables);
            notification.setStatus(NotificationStatus.SENT);
            notification.setBody("Payment " + event.status() + " email sent for subscription: " + event.subscriptionId());
            log.info("Payment notification sent to {} for subscription {} (status: {})",
                    recipientEmail, event.subscriptionId(), event.status());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("Failed to send payment notification for subscription {}: {}",
                    event.subscriptionId(), e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId.toString()));
        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getAllNotifications(Pageable pageable) {
        return notificationMapper.toPageResponse(notificationRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotificationsByTraveler(UUID travelerId, Pageable pageable) {
        return notificationMapper.toPageResponse(notificationRepository.findByTravelerId(travelerId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotificationsByTravel(UUID travelId, Pageable pageable) {
        return notificationMapper.toPageResponse(notificationRepository.findByTravelId(travelId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotificationsBySubscription(UUID subscriptionId, Pageable pageable) {
        return notificationMapper.toPageResponse(notificationRepository.findBySubscriptionId(subscriptionId, pageable));
    }

    // ---- Private helpers ----

    private String formatAmount(Double amount, String currency) {
        if (amount == null) return "N/A";
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " " + (currency != null ? currency : "XOF");
    }
}
