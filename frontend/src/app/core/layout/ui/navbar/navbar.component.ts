import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../auth/data-access/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);

  readonly mobileMenuOpen = signal(false);
  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly currentUser = this.authService.currentUser;

  readonly isTraveler = computed(() => this.authService.hasRole('TRAVELER'));
  readonly isManager = computed(() => this.authService.hasRole('MANAGER'));
  readonly isAdmin = computed(() => this.authService.hasRole('ADMIN'));

  readonly userName = computed(() => {
    const user = this.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

  readonly userInitials = computed(() => {
    const user = this.currentUser();
    return user ? `${user.firstName[0]}${user.lastName[0]}`.toUpperCase() : '';
  });

  onLogout(): void {
    this.authService.logout();
  }
}
