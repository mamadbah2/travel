import { Component, effect, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AdminService } from '../../data-access/admin.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { UserDetailCardComponent } from '../../ui/user-detail-card/user-detail-card.component';
import { UserResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-admin-user-detail-page',
  imports: [UserDetailCardComponent],
  templateUrl: './admin-user-detail-page.component.html',
  styleUrl: './admin-user-detail-page.component.css',
})
export class AdminUserDetailPageComponent {
  readonly userId = input.required<string>();
  private readonly adminService = inject(AdminService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  readonly user = signal<UserResponse | null>(null);
  readonly loading = signal(true);

  constructor() {
    effect(() => {
      const id = this.userId();
      if (id) this.loadUser(id);
    });
  }

  async onBan(): Promise<void> {
    try {
      const updated = await this.adminService.banUser(this.userId());
      this.user.set(updated);
      this.notifications.showSuccess('User banned.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to ban user.');
    }
  }

  async onUnban(): Promise<void> {
    try {
      const updated = await this.adminService.unbanUser(this.userId());
      this.user.set(updated);
      this.notifications.showSuccess('User unbanned.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to unban user.');
    }
  }

  async onDelete(): Promise<void> {
    try {
      await this.adminService.deleteUser(this.userId());
      this.notifications.showSuccess('User deleted.');
      this.router.navigateByUrl('/admin/users');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to delete user.');
    }
  }

  async onUpdateScore(score: number): Promise<void> {
    try {
      const updated = await this.adminService.updatePerformanceScore(this.userId(), score);
      this.user.set(updated);
      this.notifications.showSuccess('Performance score updated.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to update score.');
    }
  }

  private async loadUser(id: string): Promise<void> {
    this.loading.set(true);
    try {
      const user = await firstValueFrom(this.adminService.getUserById(id));
      this.user.set(user);
    } catch {
      this.user.set(null);
    } finally {
      this.loading.set(false);
    }
  }
}
