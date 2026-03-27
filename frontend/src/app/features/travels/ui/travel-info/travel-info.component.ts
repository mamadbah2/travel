import { Component, inject, input, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { TravelResponse, UserResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { DestinationListComponent } from '../destination-list/destination-list.component';
import { ActivityListComponent } from '../activity-list/activity-list.component';
import { formatDateRange } from '../../../../shared/utils/date.utils';
import { ApiConfig } from '../../../../core/config/api.config';

@Component({
  selector: 'app-travel-info',
  imports: [DecimalPipe, StatusBadgeComponent, DestinationListComponent, ActivityListComponent],
  templateUrl: './travel-info.component.html',
  styleUrl: './travel-info.component.css',
})
export class TravelInfoComponent {
  private readonly http = inject(HttpClient);
  readonly travel = input.required<TravelResponse>();
  readonly manager = signal<UserResponse | null>(null);

  constructor() {
    // Use setTimeout to let input bind first
    setTimeout(() => this.loadManager(), 0);
  }

  ngOnChanges(): void {
    this.loadManager();
  }

  dates(): string {
    return formatDateRange(this.travel().startDate, this.travel().endDate);
  }

  private async loadManager(): Promise<void> {
    try {
      const t = this.travel();
      if (t?.managerId) {
        const user = await firstValueFrom(
          this.http.get<UserResponse>(ApiConfig.users.byId(t.managerId)),
        );
        this.manager.set(user);
      }
    } catch {
      // Ignore
    }
  }
}
