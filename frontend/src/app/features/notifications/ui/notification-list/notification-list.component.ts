import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NotificationResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';

@Component({
  selector: 'app-notification-list',
  imports: [DatePipe, StatusBadgeComponent],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.css',
})
export class NotificationListComponent {
  readonly notifications = input.required<NotificationResponse[]>();
}
