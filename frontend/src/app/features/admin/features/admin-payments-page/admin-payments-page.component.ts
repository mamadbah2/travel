import { Component, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../data-access/admin.service';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { PaymentResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-admin-payments-page',
  imports: [DatePipe, DecimalPipe, StatusBadgeComponent, PaginationComponent],
  templateUrl: './admin-payments-page.component.html',
  styleUrl: './admin-payments-page.component.css',
})
export class AdminPaymentsPageComponent {
  private readonly adminService = inject(AdminService);

  readonly payments = signal<PaymentResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);

  constructor() {
    this.loadPage(0);
  }

  async loadPage(page: number): Promise<void> {
    this.loading.set(true);
    try {
      const res = await firstValueFrom(this.adminService.getAllPayments(page));
      this.payments.set(res.content);
      this.page.set(res.page);
      this.totalPages.set(res.totalPages);
    } catch {
      this.payments.set([]);
    } finally {
      this.loading.set(false);
    }
  }
}
