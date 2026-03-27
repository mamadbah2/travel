import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { PageResponse, TravelResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class TravelService {
  private readonly http = inject(HttpClient);

  getTravels(page = 0, size = 12): Observable<PageResponse<TravelResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<TravelResponse>>(ApiConfig.travels.list, { params });
  }

  getTravelById(id: string): Observable<TravelResponse> {
    return this.http.get<TravelResponse>(ApiConfig.travels.byId(id));
  }

  searchTravels(query: string, page = 0, size = 12): Observable<PageResponse<TravelResponse>> {
    const params = new HttpParams().set('search', query).set('page', page).set('size', size);
    return this.http.get<PageResponse<TravelResponse>>(ApiConfig.travels.search, { params });
  }
}
