import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-review-form',
  imports: [FormsModule],
  templateUrl: './review-form.component.html',
  styleUrl: './review-form.component.css',
})
export class ReviewFormComponent {
  readonly travelId = input.required<string>();
  readonly submitting = input(false);
  readonly submitted = output<{ rating: number; comment: string }>();

  readonly rating = signal(5);
  readonly comment = signal('');
  readonly expanded = signal(false);

  readonly stars = [1, 2, 3, 4, 5];

  setRating(value: number): void {
    this.rating.set(value);
  }

  submit(): void {
    if (!this.comment().trim()) return;
    this.submitted.emit({ rating: this.rating(), comment: this.comment() });
  }
}
