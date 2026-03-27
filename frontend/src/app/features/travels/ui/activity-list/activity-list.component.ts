import { Component, input } from '@angular/core';
import { ActivityResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-activity-list',
  templateUrl: './activity-list.component.html',
  styleUrl: './activity-list.component.css',
})
export class ActivityListComponent {
  readonly activities = input.required<ActivityResponse[]>();
}
