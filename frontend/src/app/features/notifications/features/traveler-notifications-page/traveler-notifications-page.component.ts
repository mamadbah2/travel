import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { NotificationApiService } from '../../data-access/notification-api.service';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { NotificationResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-traveler-notifications-page',
  imports: [DatePipe, StatusBadgeComponent, PaginationComponent],
  templateUrl: './traveler-notifications-page.component.html',
  styleUrl: './traveler-notifications-page.component.css',
})
export class TravelerNotificationsPageComponent {
  private readonly authService = inject(AuthService);
  private readonly notifService = inject(NotificationApiService);

  readonly notifications = signal<NotificationResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);

  constructor() {
    this.loadPage(0);
  }

  async loadPage(page: number): Promise<void> {
    this.loading.set(true);
    const user = this.authService.currentUser();
    if (!user) {
      this.loading.set(false);
      return;
    }
    try {
      const res = await this.notifService.getNotificationsByTraveler(user.id, page);
      this.notifications.set(res.content);
      this.page.set(res.page);
      this.totalPages.set(res.totalPages);
    } catch {
      this.notifications.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  getIconColor(type: string): string {
    switch (type) {
      case 'PAYMENT_SUCCESS': return 'bg-emerald-100 text-emerald-600';
      case 'PAYMENT_FAILED': return 'bg-red-100 text-red-600';
      case 'SUBSCRIPTION_CREATED': return 'bg-[#d8b4a0]/20 text-[#223843]';
      default: return 'bg-[#eff1f3] text-[#223843]/60';
    }
  }
}
