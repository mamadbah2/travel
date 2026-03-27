import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { PaymentService } from '../../data-access/payment.service';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { PaymentResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-traveler-payments-page',
  imports: [DatePipe, DecimalPipe, StatusBadgeComponent, PaginationComponent],
  templateUrl: './traveler-payments-page.component.html',
  styleUrl: './traveler-payments-page.component.css',
})
export class TravelerPaymentsPageComponent {
  private readonly authService = inject(AuthService);
  private readonly paymentService = inject(PaymentService);

  readonly payments = signal<PaymentResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);

  readonly totalSpent = computed(() =>
    this.payments().filter(p => p.status === 'SUCCESS').reduce((sum, p) => sum + p.amount, 0),
  );
  readonly successCount = computed(() => this.payments().filter(p => p.status === 'SUCCESS').length);
  readonly pendingCount = computed(() => this.payments().filter(p => p.status === 'PENDING').length);

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
      const res = await this.paymentService.getPaymentsByTraveler(user.id, page);
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
