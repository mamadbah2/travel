import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { TravelResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDateRange } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-travel-manager-card',
  imports: [RouterLink, DecimalPipe, StatusBadgeComponent],
  templateUrl: './travel-manager-card.component.html',
  styleUrl: './travel-manager-card.component.css',
})
export class TravelManagerCardComponent {
  readonly travel = input.required<TravelResponse>();
  readonly publish = output<void>();
  readonly cancelTravel = output<void>();
  readonly deleteTravel = output<void>();

  dates(): string {
    return formatDateRange(this.travel().startDate, this.travel().endDate);
  }
}
