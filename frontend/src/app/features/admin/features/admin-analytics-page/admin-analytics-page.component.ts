import { Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../../core/config/api.config';
import { PageResponse, PaymentResponse, UserResponse, TravelResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-admin-analytics-page',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './admin-analytics-page.component.html',
  styleUrl: './admin-analytics-page.component.css',
})
export class AdminAnalyticsPageComponent {
  private readonly http = inject(HttpClient);

  readonly users = signal<UserResponse[]>([]);
  readonly travels = signal<TravelResponse[]>([]);
  readonly payments = signal<PaymentResponse[]>([]);
  readonly loading = signal(true);

  readonly totalUsers = computed(() => this.users().length);
  readonly totalManagers = computed(() => this.users().filter(u => u.role === 'MANAGER').length);
  readonly totalTravelers = computed(() => this.users().filter(u => u.role === 'TRAVELER').length);
  readonly publishedTravels = computed(() => this.travels().filter(t => t.status === 'PUBLISHED').length);
  readonly totalRevenue = computed(() =>
    this.payments().filter(p => p.status === 'SUCCESS').reduce((sum, p) => sum + p.amount, 0),
  );
  readonly successPayments = computed(() => this.payments().filter(p => p.status === 'SUCCESS').length);

  readonly managerRanking = computed(() => {
    const managers = this.users().filter(u => u.role === 'MANAGER' && u.performanceScore !== null);
    return managers.sort((a, b) => (b.performanceScore ?? 0) - (a.performanceScore ?? 0));
  });

  constructor() {
    this.loadData();
  }

  private async loadData(): Promise<void> {
    try {
      const [usersRes, travelsRes, paymentsRes] = await Promise.all([
        firstValueFrom(this.http.get<PageResponse<UserResponse>>(
          ApiConfig.users.list, { params: new HttpParams().set('page', 0).set('size', 100) },
        )),
        firstValueFrom(this.http.get<PageResponse<TravelResponse>>(
          ApiConfig.travels.list, { params: new HttpParams().set('page', 0).set('size', 100) },
        )),
        firstValueFrom(this.http.get<PageResponse<PaymentResponse>>(
          ApiConfig.payments.list, { params: new HttpParams().set('page', 0).set('size', 100) },
        )),
      ]);
      this.users.set(usersRes.content);
      this.travels.set(travelsRes.content);
      this.payments.set(paymentsRes.content);
    } catch {
      // Ignore
    } finally {
      this.loading.set(false);
    }
  }
}
