import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe, DatePipe } from '@angular/common';
import { AuthService } from '../../../core/auth/data-access/auth.service';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { TravelResponse, RecommendationResponse } from '../../../shared/models/api.models';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink, DecimalPipe, DatePipe],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css',
})
export class HomePageComponent {
  private readonly authService = inject(AuthService);
  private readonly http = inject(HttpClient);

  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly currentUser = this.authService.currentUser;
  readonly isTraveler = computed(() => this.authService.hasRole('TRAVELER'));

  readonly isManager = computed(() => this.authService.hasRole('MANAGER'));
  readonly isAdmin = computed(() => this.authService.hasRole('ADMIN'));

  readonly featuredTravels = signal<TravelResponse[]>([]);
  readonly recommendations = signal<RecommendationResponse[]>([]);
  readonly loaded = signal(false);

  uniqueCountries(travel: TravelResponse): string[] {
    return [...new Set(travel.destinations.map(d => d.country))];
  }

  constructor() {
    this.loadData();
  }

  private async loadData(): Promise<void> {
    try {
      const res = await firstValueFrom(
        this.http.get<any>(ApiConfig.travels.list + '?page=0&size=6'),
      );
      this.featuredTravels.set(res.content || []);

      if (this.isAuthenticated() && this.isTraveler()) {
        const recs = await firstValueFrom(
          this.http.get<RecommendationResponse[]>(ApiConfig.recommendations.me),
        );
        this.recommendations.set(recs.slice(0, 3));
      }
    } catch {
      // Ignore errors on home page
    } finally {
      this.loaded.set(true);
    }
  }
}
