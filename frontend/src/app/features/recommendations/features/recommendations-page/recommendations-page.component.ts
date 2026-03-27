import { Component, inject, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RecommendationService } from '../../data-access/recommendation.service';
import { RecommendationResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-recommendations-page',
  imports: [DecimalPipe, DatePipe, RouterLink],
  templateUrl: './recommendations-page.component.html',
  styleUrl: './recommendations-page.component.css',
})
export class RecommendationsPageComponent {
  private readonly recService = inject(RecommendationService);

  readonly recommendations = signal<RecommendationResponse[]>([]);
  readonly loading = signal(true);

  constructor() {
    this.load();
  }

  private async load(): Promise<void> {
    try {
      const data = await this.recService.getMyRecommendations();
      this.recommendations.set(data);
    } catch {
      this.recommendations.set([]);
    } finally {
      this.loading.set(false);
    }
  }
}
