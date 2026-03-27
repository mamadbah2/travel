import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ManagerStore } from '../../data-access/manager.store';
import { ManagerService } from '../../data-access/manager.service';
import { TravelManagerCardComponent } from '../../ui/travel-manager-card/travel-manager-card.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { NotificationService } from '../../../../shared/data-access/notification.service';

@Component({
  selector: 'app-manager-dashboard-page',
  imports: [RouterLink, TravelManagerCardComponent, PaginationComponent],
  templateUrl: './manager-dashboard-page.component.html',
  styleUrl: './manager-dashboard-page.component.css',
})
export class ManagerDashboardPageComponent {
  readonly store = inject(ManagerStore);
  private readonly managerService = inject(ManagerService);
  private readonly notifications = inject(NotificationService);

  constructor() {
    this.store.loadTravels(0);
  }

  async onPublish(id: string): Promise<void> {
    try {
      const updated = await this.managerService.publishTravel(id);
      this.store.updateTravelInList(updated);
      this.notifications.showSuccess('Travel published!');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to publish travel.');
    }
  }

  async onCancel(id: string): Promise<void> {
    try {
      const updated = await this.managerService.cancelTravel(id);
      this.store.updateTravelInList(updated);
      this.notifications.showSuccess('Travel cancelled.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to cancel travel.');
    }
  }

  async onDelete(id: string): Promise<void> {
    try {
      await this.managerService.deleteTravel(id);
      this.store.removeTravel(id);
      this.notifications.showSuccess('Travel deleted.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to delete travel.');
    }
  }
}
