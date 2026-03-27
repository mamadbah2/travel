import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../../core/config/api.config';
import { ChangePasswordRequest, UserResponse } from '../../../../shared/models/api.models';
import { NotificationService } from '../../../../shared/data-access/notification.service';

@Component({
  selector: 'app-profile-page',
  imports: [FormsModule, DatePipe],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css',
})
export class ProfilePageComponent {
  private readonly authService = inject(AuthService);
  private readonly http = inject(HttpClient);
  private readonly notify = inject(NotificationService);

  readonly user = this.authService.currentUser;
  readonly editMode = signal(false);
  readonly changingPassword = signal(false);
  readonly saving = signal(false);

  editForm = { firstName: '', lastName: '', phoneNumber: '' };
  passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };

  startEdit(): void {
    const u = this.user();
    if (!u) return;
    this.editForm = {
      firstName: u.firstName,
      lastName: u.lastName,
      phoneNumber: u.phoneNumber || '',
    };
    this.editMode.set(true);
  }

  cancelEdit(): void {
    this.editMode.set(false);
  }

  async saveProfile(): Promise<void> {
    const u = this.user();
    if (!u) return;
    this.saving.set(true);
    try {
      await firstValueFrom(
        this.http.put<UserResponse>(ApiConfig.users.byId(u.id), this.editForm),
      );
      this.notify.showSuccess('Profile updated successfully');
      this.editMode.set(false);
    } catch {
      this.notify.showError('Failed to update profile');
    } finally {
      this.saving.set(false);
    }
  }

  async changePassword(): Promise<void> {
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.notify.showError('Passwords do not match');
      return;
    }
    if (this.passwordForm.newPassword.length < 6) {
      this.notify.showError('Password must be at least 6 characters');
      return;
    }
    this.saving.set(true);
    try {
      const body: ChangePasswordRequest = {
        currentPassword: this.passwordForm.currentPassword,
        newPassword: this.passwordForm.newPassword,
      };
      await firstValueFrom(
        this.http.put(ApiConfig.users.changePassword, body),
      );
      this.notify.showSuccess('Password changed successfully');
      this.changingPassword.set(false);
      this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
    } catch {
      this.notify.showError('Failed to change password');
    } finally {
      this.saving.set(false);
    }
  }
}
