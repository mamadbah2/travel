import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SubscriptionResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-subscription-card',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './subscription-card.component.html',
  styleUrl: './subscription-card.component.css',
})
export class SubscriptionCardComponent {
  readonly subscription = input.required<SubscriptionResponse>();

  date(): string {
    return formatDate(this.subscription().createdAt);
  }
}
