import { Component, effect, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ManagerService } from '../../data-access/manager.service';
import { TravelService } from '../../../travels/data-access/travel.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { TravelFormComponent } from '../../ui/travel-form/travel-form.component';
import { CreateTravelRequest, TravelResponse } from '../../../../shared/models/api.models';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-travel-edit-page',
  imports: [TravelFormComponent],
  templateUrl: './travel-edit-page.component.html',
  styleUrl: './travel-edit-page.component.css',
})
export class TravelEditPageComponent {
  readonly travelId = input.required<string>();
  private readonly managerService = inject(ManagerService);
  private readonly travelService = inject(TravelService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly travel = signal<TravelResponse | null>(null);
  readonly loadingTravel = signal(true);
  readonly saving = signal(false);

  constructor() {
    effect(() => {
      const id = this.travelId();
      if (id) {
        this.loadTravel(id);
      }
    });
  }

  async onSave(request: CreateTravelRequest): Promise<void> {
    this.saving.set(true);
    try {
      await this.managerService.updateTravel(this.travelId(), request);
      this.notifications.showSuccess('Travel updated!');
      this.router.navigateByUrl('/manager');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to update travel.');
    } finally {
      this.saving.set(false);
    }
  }

  private async loadTravel(id: string): Promise<void> {
    this.loadingTravel.set(true);
    try {
      const travel = await firstValueFrom(this.travelService.getTravelById(id));
      this.travel.set(travel);
    } catch {
      this.travel.set(null);
    } finally {
      this.loadingTravel.set(false);
    }
  }
}
