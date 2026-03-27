import { Component, input, output } from '@angular/core';
import { SubscriptionResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-subscriber-list',
  imports: [StatusBadgeComponent],
  templateUrl: './subscriber-list.component.html',
  styleUrl: './subscriber-list.component.css',
})
export class SubscriberListComponent {
  readonly subscribers = input.required<SubscriptionResponse[]>();
  readonly remove = output<string>();

  formatDate = formatDate;
}
