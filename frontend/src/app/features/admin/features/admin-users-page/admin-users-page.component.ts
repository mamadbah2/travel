import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminUsersStore } from '../../data-access/admin-users.store';
import { AdminService } from '../../data-access/admin.service';
import { UserTableComponent } from '../../ui/user-table/user-table.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { UserRole, UserStatus } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-admin-users-page',
  imports: [FormsModule, UserTableComponent, PaginationComponent],
  templateUrl: './admin-users-page.component.html',
  styleUrl: './admin-users-page.component.css',
})
export class AdminUsersPageComponent {
  readonly store = inject(AdminUsersStore);
  private readonly adminService = inject(AdminService);
  private readonly notifications = inject(NotificationService);

  readonly searchInput = signal('');
  readonly roleFilter = signal<UserRole | null>(null);
  readonly statusFilter = signal<UserStatus | null>(null);
  private searchTimeout: ReturnType<typeof setTimeout> | null = null;

  constructor() {
    this.store.loadUsers({});
  }

  onSearchChange(value: string): void {
    this.searchInput.set(value);
    this.roleFilter.set(null);
    this.statusFilter.set(null);
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.store.loadUsers({ search: value, role: null, status: null, page: 0 });
    }, 300);
  }

  onRoleFilter(role: UserRole | null): void {
    this.roleFilter.set(role);
    this.searchInput.set('');
    this.statusFilter.set(null);
    this.store.loadUsers({ role, search: '', status: null, page: 0 });
  }

  onStatusFilter(status: UserStatus | null): void {
    this.statusFilter.set(status);
    this.searchInput.set('');
    this.roleFilter.set(null);
    this.store.loadUsers({ status, search: '', role: null, page: 0 });
  }

  clearFilters(): void {
    this.searchInput.set('');
    this.roleFilter.set(null);
    this.statusFilter.set(null);
    this.store.clearFilters();
    this.store.loadUsers({ search: '', role: null, status: null, page: 0 });
  }

  async onBan(id: string): Promise<void> {
    try {
      const updated = await this.adminService.banUser(id);
      this.store.updateUserInList(updated);
      this.notifications.showSuccess('User banned.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to ban user.');
    }
  }

  async onUnban(id: string): Promise<void> {
    try {
      const updated = await this.adminService.unbanUser(id);
      this.store.updateUserInList(updated);
      this.notifications.showSuccess('User unbanned.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to unban user.');
    }
  }

  async onDelete(id: string): Promise<void> {
    try {
      await this.adminService.deleteUser(id);
      this.store.removeUserFromList(id);
      this.notifications.showSuccess('User deleted.');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Failed to delete user.');
    }
  }
}
