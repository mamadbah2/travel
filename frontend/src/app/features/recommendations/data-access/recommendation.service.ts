import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { RecommendationResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class RecommendationService {
  private readonly http = inject(HttpClient);

  getMyRecommendations(): Promise<RecommendationResponse[]> {
    return firstValueFrom(
      this.http.get<RecommendationResponse[]>(ApiConfig.recommendations.me),
    );
  }
}
