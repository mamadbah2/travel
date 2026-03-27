import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { NotificationResponse, PageResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private readonly http = inject(HttpClient);

  getNotificationsByTraveler(
    travelerId: string,
    page = 0,
    size = 20,
  ): Promise<PageResponse<NotificationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<NotificationResponse>>(
        ApiConfig.notifications.byTraveler(travelerId),
        { params },
      ),
    );
  }

  getNotificationsByTravel(
    travelId: string,
    page = 0,
    size = 20,
  ): Promise<PageResponse<NotificationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<NotificationResponse>>(
        ApiConfig.notifications.byTravel(travelId),
        { params },
      ),
    );
  }

  getNotificationsBySubscription(
    subscriptionId: string,
    page = 0,
    size = 20,
  ): Promise<PageResponse<NotificationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<NotificationResponse>>(
        ApiConfig.notifications.bySubscription(subscriptionId),
        { params },
      ),
    );
  }
}
