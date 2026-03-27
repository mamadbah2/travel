import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../data-access/admin.service';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { NotificationResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-admin-notifications-page',
  imports: [DatePipe, StatusBadgeComponent, PaginationComponent],
  templateUrl: './admin-notifications-page.component.html',
  styleUrl: './admin-notifications-page.component.css',
})
export class AdminNotificationsPageComponent {
  private readonly adminService = inject(AdminService);

  readonly notifications = signal<NotificationResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);

  constructor() {
    this.loadPage(0);
  }

  async loadPage(page: number): Promise<void> {
    this.loading.set(true);
    try {
      const res = await firstValueFrom(this.adminService.getAllNotifications(page));
      this.notifications.set(res.content);
      this.page.set(res.page);
      this.totalPages.set(res.totalPages);
    } catch {
      this.notifications.set([]);
    } finally {
      this.loading.set(false);
    }
  }
}
