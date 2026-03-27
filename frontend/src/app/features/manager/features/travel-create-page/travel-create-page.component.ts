import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ManagerService } from '../../data-access/manager.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { TravelFormComponent } from '../../ui/travel-form/travel-form.component';
import { CreateTravelRequest } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-travel-create-page',
  imports: [TravelFormComponent],
  templateUrl: './travel-create-page.component.html',
  styleUrl: './travel-create-page.component.css',
})
export class TravelCreatePageComponent {
  private readonly managerService = inject(ManagerService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly loading = signal(false);

  async onCreate(request: CreateTravelRequest): Promise<void> {
    this.loading.set(true);
    try {
      const travel = await this.managerService.createTravel(request);
      this.notifications.showSuccess('Travel created as draft!');
      this.router.navigate(['/manager', travel.id, 'edit']);
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to create travel.');
    } finally {
      this.loading.set(false);
    }
  }
}
