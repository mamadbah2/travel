import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { CreateReviewRequest, PageResponse, ReviewResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);

  getReviewsByTravel(travelId: string, page = 0, size = 10): Promise<PageResponse<ReviewResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<ReviewResponse>>(ApiConfig.reviews.byTravel(travelId), { params }),
    );
  }

  getReviewsByTraveler(travelerId: string, page = 0, size = 10): Promise<PageResponse<ReviewResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<ReviewResponse>>(ApiConfig.reviews.byTraveler(travelerId), { params }),
    );
  }

  createReview(request: CreateReviewRequest): Promise<ReviewResponse> {
    return firstValueFrom(
      this.http.post<ReviewResponse>(ApiConfig.reviews.create, request),
    );
  }
}
