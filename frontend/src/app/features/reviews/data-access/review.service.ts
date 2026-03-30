import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { CreateFeedbackRequest, FeedbackResponse, PageResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);

  getReviewsByTravel(travelId: string, page = 0, size = 20): Promise<PageResponse<FeedbackResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<FeedbackResponse>>(ApiConfig.feedbacks.byTravel(travelId), { params }),
    );
  }

  getMyReviews(page = 0, size = 20): Promise<PageResponse<FeedbackResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<FeedbackResponse>>(ApiConfig.feedbacks.me, { params }),
    );
  }

  createReview(request: CreateFeedbackRequest): Promise<FeedbackResponse> {
    return firstValueFrom(
      this.http.post<FeedbackResponse>(ApiConfig.feedbacks.create, request),
    );
  }
}
