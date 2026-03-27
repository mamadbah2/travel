import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { SubscriptionStore } from '../../data-access/subscription.store';
import { SubscriptionInfoComponent } from '../../ui/subscription-info/subscription-info.component';
import { CancelButtonComponent } from '../../ui/cancel-button/cancel-button.component';
import { PaymentStatusComponent } from '../../../payments/ui/payment-status/payment-status.component';
import { NotificationListComponent } from '../../../notifications/ui/notification-list/notification-list.component';
import { PaymentService } from '../../../payments/data-access/payment.service';
import { NotificationApiService } from '../../../notifications/data-access/notification-api.service';
import { SubscriptionService } from '../../data-access/subscription.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { NotificationResponse, PaymentMethod, PaymentResponse } from '../../../../shared/models/api.models';
import { canCancelSubscription } from '../../../../shared/utils/date.utils';
import { TravelService } from '../../../travels/data-access/travel.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-subscription-detail-page',
  imports: [
    SubscriptionInfoComponent,
    CancelButtonComponent,
    PaymentStatusComponent,
    NotificationListComponent,
  ],
  templateUrl: './subscription-detail-page.component.html',
  styleUrl: './subscription-detail-page.component.css',
})
export class SubscriptionDetailPageComponent {
  readonly subscriptionId = input.required<string>();
  readonly store = inject(SubscriptionStore);
  private readonly paymentService = inject(PaymentService);
  private readonly notificationApi = inject(NotificationApiService);
  private readonly subscriptionService = inject(SubscriptionService);
  private readonly travelService = inject(TravelService);
  private readonly toasts = inject(NotificationService);

  readonly payment = signal<PaymentResponse | null>(null);
  readonly paymentLoading = signal(false);
  readonly notifications = signal<NotificationResponse[]>([]);
  readonly cancelling = signal(false);
  readonly paying = signal(false);
  readonly selectedMethod = signal<PaymentMethod | null>(null);
  readonly travelStartDate = signal<string | null>(null);
  readonly travelPrice = signal<number>(0);

  readonly paymentMethods: { value: PaymentMethod; label: string; icon: string }[] = [
    { value: 'STRIPE', label: 'Stripe', icon: 'M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z' },
    { value: 'PAYPAL', label: 'PayPal', icon: 'M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z' },
    { value: 'WAVE', label: 'Wave', icon: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z' },
  ];

  selectMethod(method: PaymentMethod): void {
    this.selectedMethod.set(method);
  }

  readonly cancellationInfo = computed(() => {
    const date = this.travelStartDate();
    return date ? canCancelSubscription(date) : null;
  });

  constructor() {
    effect(() => {
      const id = this.subscriptionId();
      if (id) {
        this.store.loadSubscription(id);
        this.loadPayment(id);
        this.loadNotifications(id);
      }
    });

    // Load travel start date when subscription is available
    effect(() => {
      const sub = this.store.selectedSubscription();
      if (sub) {
        firstValueFrom(this.travelService.getTravelById(sub.travelId))
          .then((travel) => {
            this.travelStartDate.set(travel.startDate);
            this.travelPrice.set(travel.price);
          })
          .catch(() => {});
      }
    });
  }

  async onCancel(): Promise<void> {
    this.cancelling.set(true);
    try {
      await this.subscriptionService.cancelSubscription(this.subscriptionId());
      this.store.updateSelectedStatus('CANCELLED');
      this.toasts.showSuccess('Subscription cancelled successfully.');
    } catch (err: any) {
      this.toasts.showError(err?.error?.message || 'Failed to cancel subscription.');
    } finally {
      this.cancelling.set(false);
    }
  }

  async simulatePayment(): Promise<void> {
    this.paying.set(true);
    try {
      // Simulate a short processing delay
      await new Promise(resolve => setTimeout(resolve, 1500));
      // After "payment", reload data to get the mock payment
      this.store.updateSelectedStatus('CONFIRMED');
      this.toasts.showSuccess('Payment processed successfully!');
      this.refreshData();
    } catch {
      this.toasts.showError('Payment failed. Please try again.');
    } finally {
      this.paying.set(false);
    }
  }

  refreshData(): void {
    const id = this.subscriptionId();
    this.store.loadSubscription(id);
    this.loadPayment(id);
    this.loadNotifications(id);
  }

  private async loadPayment(subscriptionId: string): Promise<void> {
    this.paymentLoading.set(true);
    try {
      const p = await this.paymentService.getPaymentBySubscription(subscriptionId);
      this.payment.set(p);
    } catch {
      this.payment.set(null);
    } finally {
      this.paymentLoading.set(false);
    }
  }

  private async loadNotifications(subscriptionId: string): Promise<void> {
    try {
      const page = await this.notificationApi.getNotificationsBySubscription(subscriptionId);
      this.notifications.set(page.content);
    } catch {
      this.notifications.set([]);
    }
  }
}
