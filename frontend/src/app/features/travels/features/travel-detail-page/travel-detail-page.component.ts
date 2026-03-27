import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { TravelStore } from '../../data-access/travel.store';
import { TravelInfoComponent } from '../../ui/travel-info/travel-info.component';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { SubscriptionService } from '../../../subscriptions/data-access/subscription.service';
import { ReviewService } from '../../../reviews/data-access/review.service';
import { ReviewListComponent } from '../../../reviews/ui/review-list/review-list.component';
import { ReviewFormComponent } from '../../../reviews/ui/review-form/review-form.component';
import { ReviewResponse, UserResponse } from '../../../../shared/models/api.models';
import { HttpClient } from '@angular/common/http';
import { ApiConfig } from '../../../../core/config/api.config';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-travel-detail-page',
  imports: [TravelInfoComponent, ReviewListComponent, ReviewFormComponent, FormsModule],
  templateUrl: './travel-detail-page.component.html',
  styleUrl: './travel-detail-page.component.css',
})
export class TravelDetailPageComponent {
  readonly travelId = input.required<string>();
  readonly store = inject(TravelStore);
  readonly authService = inject(AuthService);
  private readonly subscriptionService = inject(SubscriptionService);
  private readonly reviewService = inject(ReviewService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  private readonly http = inject(HttpClient);

  readonly isTraveler = computed(() => this.authService.hasRole('TRAVELER'));
  readonly subscribing = computed(() => false);
  readonly reviews = signal<ReviewResponse[]>([]);
  readonly averageRating = signal(0);
  readonly reviewSubmitting = signal(false);
  readonly manager = signal<UserResponse | null>(null);
  readonly managerRating = signal(50);
  readonly ratingSubmitting = signal(false);
  readonly ratingSubmitted = signal(false);

  readonly canSubscribe = computed(() => {
    const travel = this.store.selectedTravel();
    return (
      this.isTraveler() &&
      travel !== null &&
      travel.status === 'PUBLISHED' &&
      travel.availableSpots > 0
    );
  });

  constructor() {
    effect(() => {
      const id = this.travelId();
      if (id) {
        this.store.loadTravel(id);
        this.loadReviews(id);
      }
    });

    // Load manager when travel is available
    effect(() => {
      const travel = this.store.selectedTravel();
      if (travel?.managerId) {
        this.loadManager(travel.managerId);
      }
    });
  }

  async onSubscribe(): Promise<void> {
    try {
      const sub = await this.subscriptionService.subscribe(this.travelId());
      this.notifications.showSuccess('Successfully subscribed! Redirecting to your subscription...');
      this.router.navigate(['/subscriptions', sub.id]);
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Subscription failed.');
    }
  }

  async onReviewSubmit(data: { rating: number; comment: string }): Promise<void> {
    this.reviewSubmitting.set(true);
    try {
      const review = await this.reviewService.createReview({
        travelId: this.travelId(),
        rating: data.rating,
        comment: data.comment,
      });
      this.reviews.update(list => [review, ...list]);
      this.recalcAverage();
      this.notifications.showSuccess('Review submitted!');
    } catch {
      this.notifications.showError('Failed to submit review');
    } finally {
      this.reviewSubmitting.set(false);
    }
  }

  private async loadReviews(travelId: string): Promise<void> {
    try {
      const res = await this.reviewService.getReviewsByTravel(travelId);
      this.reviews.set(res.content);
      this.recalcAverage();
    } catch {
      this.reviews.set([]);
    }
  }

  async submitManagerRating(): Promise<void> {
    const mgr = this.manager();
    if (!mgr) return;
    this.ratingSubmitting.set(true);
    try {
      await firstValueFrom(
        this.http.put(ApiConfig.users.performanceScore(mgr.id), { performanceScore: this.managerRating() }),
      );
      this.notifications.showSuccess(`Manager rated ${this.managerRating()}/100`);
      this.ratingSubmitted.set(true);
    } catch {
      this.notifications.showError('Failed to submit rating');
    } finally {
      this.ratingSubmitting.set(false);
    }
  }

  private async loadManager(managerId: string): Promise<void> {
    try {
      const user = await firstValueFrom(
        this.http.get<UserResponse>(ApiConfig.users.byId(managerId)),
      );
      this.manager.set(user);
      if (user.performanceScore !== null) {
        this.managerRating.set(user.performanceScore);
      }
    } catch {
      // Ignore
    }
  }

  private recalcAverage(): void {
    const list = this.reviews();
    if (list.length === 0) {
      this.averageRating.set(0);
      return;
    }
    this.averageRating.set(list.reduce((sum, r) => sum + r.rating, 0) / list.length);
  }
}
