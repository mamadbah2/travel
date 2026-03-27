import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import {
  CreateTravelRequest,
  MessageResponse,
  PageResponse,
  SubscriptionResponse,
  TravelResponse,
  UpdateTravelRequest,
} from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class ManagerService {
  private readonly http = inject(HttpClient);

  getMyTravels(page = 0, size = 20): Observable<PageResponse<TravelResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<TravelResponse>>(ApiConfig.travels.managerMe, { params });
  }

  createTravel(request: CreateTravelRequest): Promise<TravelResponse> {
    return firstValueFrom(
      this.http.post<TravelResponse>(ApiConfig.travels.list, request),
    );
  }

  updateTravel(id: string, request: UpdateTravelRequest): Promise<TravelResponse> {
    return firstValueFrom(
      this.http.put<TravelResponse>(ApiConfig.travels.byId(id), request),
    );
  }

  deleteTravel(id: string): Promise<MessageResponse> {
    return firstValueFrom(
      this.http.delete<MessageResponse>(ApiConfig.travels.byId(id)),
    );
  }

  publishTravel(id: string): Promise<TravelResponse> {
    return firstValueFrom(
      this.http.post<TravelResponse>(ApiConfig.travels.publish(id), null),
    );
  }

  cancelTravel(id: string): Promise<TravelResponse> {
    return firstValueFrom(
      this.http.post<TravelResponse>(ApiConfig.travels.cancel(id), null),
    );
  }

  getSubscribers(
    travelId: string,
    page = 0,
    size = 20,
  ): Observable<PageResponse<SubscriptionResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SubscriptionResponse>>(
      ApiConfig.travels.subscribers(travelId),
      { params },
    );
  }

  removeSubscriber(travelId: string, subscriptionId: string): Promise<MessageResponse> {
    return firstValueFrom(
      this.http.delete<MessageResponse>(
        ApiConfig.travels.removeSubscriber(travelId, subscriptionId),
      ),
    );
  }
}
