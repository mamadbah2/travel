import { Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SearchResultResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDateRange } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-search-result-card',
  imports: [DecimalPipe, RouterLink, StatusBadgeComponent],
  templateUrl: './search-result-card.component.html',
  styleUrl: './search-result-card.component.css',
})
export class SearchResultCardComponent {
  readonly result = input.required<SearchResultResponse>();

  destinationNames(): string {
    return this.result().destinations.map((d) => d.name).join(', ');
  }

  dates(): string {
    return formatDateRange(this.result().startDate, this.result().endDate);
  }
}
