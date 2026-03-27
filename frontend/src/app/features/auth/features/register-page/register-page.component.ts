import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { RegisterRequest } from '../../../../shared/models/api.models';
import { RegisterFormComponent } from '../../ui/register-form/register-form.component';

@Component({
  selector: 'app-register-page',
  imports: [RegisterFormComponent, RouterLink],
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.css',
})
export class RegisterPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly loading = signal(false);

  async onRegister(request: RegisterRequest): Promise<void> {
    this.loading.set(true);
    try {
      await this.authService.register(request);
      this.notifications.showSuccess('Account created successfully!');
      this.router.navigateByUrl('/travels');
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Registration failed. Please try again.');
    } finally {
      this.loading.set(false);
    }
  }
}
