import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { PageResponse, SearchResultResponse } from '../../../shared/models/api.models';

export interface SearchFilters {
  q?: string;
  minPrice?: number | null;
  maxPrice?: number | null;
  fromDate?: string | null;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class SearchService {
  private readonly http = inject(HttpClient);

  search(filters: SearchFilters): Observable<PageResponse<SearchResultResponse>> {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', filters.size ?? 12);

    if (filters.q) params = params.set('q', filters.q);
    if (filters.minPrice != null) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null) params = params.set('maxPrice', filters.maxPrice);
    if (filters.fromDate) params = params.set('fromDate', filters.fromDate);

    return this.http.get<PageResponse<SearchResultResponse>>(ApiConfig.search.query, { params });
  }

  getById(travelId: string): Observable<SearchResultResponse> {
    return this.http.get<SearchResultResponse>(ApiConfig.search.byId(travelId));
  }
}
