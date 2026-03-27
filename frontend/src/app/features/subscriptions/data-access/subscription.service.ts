import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { PageResponse, SubscriptionResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class SubscriptionService {
  private readonly http = inject(HttpClient);

  getSubscriptions(page = 0, size = 20): Observable<PageResponse<SubscriptionResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SubscriptionResponse>>(ApiConfig.subscriptions.list, {
      params,
    });
  }

  getSubscriptionById(id: string): Observable<SubscriptionResponse> {
    return this.http.get<SubscriptionResponse>(ApiConfig.subscriptions.byId(id));
  }

  async subscribe(travelId: string): Promise<SubscriptionResponse> {
    return firstValueFrom(
      this.http.post<SubscriptionResponse>(ApiConfig.subscriptions.create(travelId), null),
    );
  }

  async cancelSubscription(id: string): Promise<SubscriptionResponse> {
    return firstValueFrom(
      this.http.post<SubscriptionResponse>(ApiConfig.subscriptions.cancel(id), null),
    );
  }
}
