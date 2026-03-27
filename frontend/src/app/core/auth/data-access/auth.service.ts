import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../config/api.config';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserResponse,
  UserRole,
} from '../../../shared/models/api.models';

const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private readonly currentUserSignal = signal<UserResponse | null>(null);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.currentUserSignal());

  constructor() {
    this.hydrateFromStorage();
  }

  async login(request: LoginRequest): Promise<AuthResponse> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(ApiConfig.auth.login, request),
    );
    this.storeAuth(response);
    return response;
  }

  async register(request: RegisterRequest): Promise<AuthResponse> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(ApiConfig.auth.register, request),
    );
    this.storeAuth(response);
    return response;
  }

  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(ApiConfig.auth.refresh, { refreshToken }),
    );
    this.storeAuth(response);
    return response;
  }

  async logout(): Promise<void> {
    try {
      await firstValueFrom(this.http.post(ApiConfig.auth.logout, null));
    } catch {
      // Proceed with local cleanup even if server logout fails
    }
    this.clearAuth();
    this.router.navigateByUrl('/auth/login');
  }

  getAccessToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  hasRole(roles: UserRole | UserRole[]): boolean {
    const user = this.currentUserSignal();
    if (!user) return false;
    const roleArray = Array.isArray(roles) ? roles : [roles];
    return roleArray.includes(user.role);
  }

  private storeAuth(response: AuthResponse): void {
    if (!this.isBrowser) return;
    localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
    this.currentUserSignal.set(response.user);
  }

  private clearAuth(): void {
    if (this.isBrowser) {
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
    }
    this.currentUserSignal.set(null);
  }

  private hydrateFromStorage(): void {
    if (!this.isBrowser) return;
    const token = this.getAccessToken();
    if (!token) return;

    // Decode JWT payload to restore user without an API call
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Check if token is expired
      if (payload.exp && payload.exp * 1000 < Date.now()) {
        this.clearAuth();
        return;
      }
      // Fetch fresh user data from server
      firstValueFrom(this.http.get<UserResponse>(ApiConfig.users.me))
        .then((user) => this.currentUserSignal.set(user))
        .catch(() => this.clearAuth());
    } catch {
      this.clearAuth();
    }
  }
}
