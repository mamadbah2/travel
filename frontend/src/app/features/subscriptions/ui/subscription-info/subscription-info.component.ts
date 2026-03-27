import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SubscriptionResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-subscription-info',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './subscription-info.component.html',
  styleUrl: './subscription-info.component.css',
})
export class SubscriptionInfoComponent {
  readonly subscription = input.required<SubscriptionResponse>();

  createdDate(): string {
    return formatDate(this.subscription().createdAt);
  }

  updatedDate(): string {
    return formatDate(this.subscription().updatedAt);
  }
}
