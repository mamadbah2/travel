import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FeedbackResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-review-list',
  imports: [DatePipe],
  templateUrl: './review-list.component.html',
  styleUrl: './review-list.component.css',
})
export class ReviewListComponent {
  readonly reviews = input.required<FeedbackResponse[]>();
  readonly averageRating = input(0);

  readonly Math = Math;

  starsArray(rating: number): boolean[] {
    return Array.from({ length: 5 }, (_, i) => i < Math.round(rating));
  }
}
