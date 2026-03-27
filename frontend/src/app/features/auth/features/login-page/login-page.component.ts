import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/auth/data-access/auth.service';
import { NotificationService } from '../../../../shared/data-access/notification.service';
import { LoginRequest } from '../../../../shared/models/api.models';
import { LoginFormComponent } from '../../ui/login-form/login-form.component';

@Component({
  selector: 'app-login-page',
  imports: [LoginFormComponent, RouterLink],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
})
export class LoginPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly notifications = inject(NotificationService);

  readonly loading = signal(false);

  async onLogin(request: LoginRequest): Promise<void> {
    this.loading.set(true);
    try {
      await this.authService.login(request);
      const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/travels';
      this.router.navigateByUrl(returnUrl);
    } catch (err: any) {
      this.notifications.showError(err?.error?.message || 'Login failed. Please check your credentials.');
    } finally {
      this.loading.set(false);
    }
  }
}
