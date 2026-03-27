import { Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../../core/config/api.config';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { TravelResponse, PaymentResponse, SubscriptionResponse, PageResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-manager-analytics-page',
  imports: [DecimalPipe, DatePipe, RouterLink],
  templateUrl: './manager-analytics-page.component.html',
  styleUrl: './manager-analytics-page.component.css',
})
export class ManagerAnalyticsPageComponent {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  readonly travels = signal<TravelResponse[]>([]);
  readonly payments = signal<PaymentResponse[]>([]);
  readonly loading = signal(true);

  readonly totalTravels = computed(() => this.travels().length);
  readonly publishedCount = computed(() => this.travels().filter(t => t.status === 'PUBLISHED').length);
  readonly draftCount = computed(() => this.travels().filter(t => t.status === 'DRAFT').length);
  readonly totalBookings = computed(() => this.travels().reduce((sum, t) => sum + t.currentBookings, 0));
  readonly totalCapacity = computed(() => this.travels().reduce((sum, t) => sum + t.maxCapacity, 0));
  readonly occupancyRate = computed(() => {
    const cap = this.totalCapacity();
    return cap > 0 ? (this.totalBookings() / cap) * 100 : 0;
  });
  readonly totalRevenue = computed(() =>
    this.payments().filter(p => p.status === 'SUCCESS').reduce((sum, p) => sum + p.amount, 0),
  );
  readonly performanceScore = computed(() => this.authService.currentUser()?.performanceScore ?? null);

  constructor() {
    this.loadData();
  }

  private async loadData(): Promise<void> {
    try {
      const params = new HttpParams().set('page', 0).set('size', 100);
      const travelsRes = await firstValueFrom(
        this.http.get<PageResponse<TravelResponse>>(ApiConfig.travels.managerMe, { params }),
      );
      this.travels.set(travelsRes.content);

      // Load payments for each travel
      const allPayments: PaymentResponse[] = [];
      for (const travel of travelsRes.content) {
        try {
          const payRes = await firstValueFrom(
            this.http.get<PageResponse<PaymentResponse>>(ApiConfig.payments.byTravel(travel.id), { params }),
          );
          allPayments.push(...payRes.content);
        } catch { /* skip */ }
      }
      this.payments.set(allPayments);
    } catch {
      // Ignore
    } finally {
      this.loading.set(false);
    }
  }
}
