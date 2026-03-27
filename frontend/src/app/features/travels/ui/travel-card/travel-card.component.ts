import { Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TravelResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDateRange } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-travel-card',
  imports: [DecimalPipe, RouterLink, StatusBadgeComponent],
  templateUrl: './travel-card.component.html',
  styleUrl: './travel-card.component.css',
})
export class TravelCardComponent {
  readonly travel = input.required<TravelResponse>();

  destinationNames(): string {
    return this.travel()
      .destinations.map((d) => d.name)
      .join(', ');
  }

  dates(): string {
    return formatDateRange(this.travel().startDate, this.travel().endDate);
  }
}
