import { Component, effect, inject, input, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ManagerService } from '../../data-access/manager.service';
import { TravelService } from '../../../travels/data-access/travel.service';
import { PaymentService } from '../../../payments/data-access/payment.service';
import { NotificationApiService } from '../../../notifications/data-access/notification-api.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { SubscriberListComponent } from '../../ui/subscriber-list/subscriber-list.component';
import { PaymentStatusComponent } from '../../../payments/ui/payment-status/payment-status.component';
import { NotificationListComponent } from '../../../notifications/ui/notification-list/notification-list.component';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import {
  NotificationResponse,
  PaymentResponse,
  SubscriptionResponse,
  TravelResponse,
} from '../../../../shared/models/api.models';

@Component({
  selector: 'app-travel-manage-page',
  imports: [
    DecimalPipe,
    RouterLink,
    SubscriberListComponent,
    NotificationListComponent,
    StatusBadgeComponent,
  ],
  templateUrl: './travel-manage-page.component.html',
  styleUrl: './travel-manage-page.component.css',
})
export class TravelManagePageComponent {
  readonly travelId = input.required<string>();
  private readonly managerService = inject(ManagerService);
  private readonly travelService = inject(TravelService);
  private readonly notificationApi = inject(NotificationApiService);
  private readonly toasts = inject(NotificationService);

  readonly travel = signal<TravelResponse | null>(null);
  readonly subscribers = signal<SubscriptionResponse[]>([]);
  readonly payments = signal<PaymentResponse[]>([]);
  readonly notifications = signal<NotificationResponse[]>([]);
  readonly loading = signal(true);

  constructor() {
    effect(() => {
      const id = this.travelId();
      if (id) {
        this.loadAll(id);
      }
    });
  }

  async onRemoveSubscriber(subscriptionId: string): Promise<void> {
    try {
      await this.managerService.removeSubscriber(this.travelId(), subscriptionId);
      this.subscribers.update((subs) => subs.filter((s) => s.id !== subscriptionId));
      this.toasts.showSuccess('Subscriber removed.');
    } catch (err: any) {
      this.toasts.showError(err?.error?.message || 'Failed to remove subscriber.');
    }
  }

  private async loadAll(travelId: string): Promise<void> {
    this.loading.set(true);
    try {
      const [travel, subsPage, notifsPage] = await Promise.all([
        firstValueFrom(this.travelService.getTravelById(travelId)),
        firstValueFrom(this.managerService.getSubscribers(travelId)),
        this.notificationApi.getNotificationsByTravel(travelId).catch(() => ({ content: [] })),
      ]);
      this.travel.set(travel);
      this.subscribers.set(subsPage.content);
      this.notifications.set((notifsPage as any).content ?? []);
    } catch {
      this.travel.set(null);
    } finally {
      this.loading.set(false);
    }
  }
}
