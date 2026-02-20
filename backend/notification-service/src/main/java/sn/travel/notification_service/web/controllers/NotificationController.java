package sn.travel.notification_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sn.travel.notification_service.web.dto.responses.NotificationResponse;
import sn.travel.notification_service.web.dto.responses.PageResponse;

import java.util.UUID;

/**
 * REST API for consulting notifications (read-only).
 * Notifications are created automatically by RabbitMQ event listeners.
 */
@Tag(name = "Notifications", description = "Notification consultation API (read-only, notifications are event-driven)")
public interface NotificationController {

    @Operation(summary = "Get notification by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    ResponseEntity<NotificationResponse> getNotificationById(
            @Parameter(description = "Notification UUID") UUID notificationId
    );

    @Operation(summary = "Get all notifications (Admin)")
    @ApiResponse(responseCode = "200", description = "Paginated list of all notifications")
    ResponseEntity<PageResponse<NotificationResponse>> getAllNotifications(Pageable pageable);

    @Operation(summary = "Get notifications by traveler ID")
    @ApiResponse(responseCode = "200", description = "Paginated list of traveler's notifications")
    ResponseEntity<PageResponse<NotificationResponse>> getNotificationsByTraveler(
            @Parameter(description = "Traveler UUID") UUID travelerId,
            Pageable pageable
    );

    @Operation(summary = "Get notifications by travel ID")
    @ApiResponse(responseCode = "200", description = "Paginated list of travel's notifications")
    ResponseEntity<PageResponse<NotificationResponse>> getNotificationsByTravel(
            @Parameter(description = "Travel UUID") UUID travelId,
            Pageable pageable
    );

    @Operation(summary = "Get notifications by subscription ID")
    @ApiResponse(responseCode = "200", description = "Paginated list of subscription's notifications")
    ResponseEntity<PageResponse<NotificationResponse>> getNotificationsBySubscription(
            @Parameter(description = "Subscription UUID") UUID subscriptionId,
            Pageable pageable
    );
}
