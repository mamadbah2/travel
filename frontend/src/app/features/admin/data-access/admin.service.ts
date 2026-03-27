import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import {
  MessageResponse,
  NotificationResponse,
  PageResponse,
  PaymentResponse,
  UpdateUserRequest,
  UserResponse,
  UserRole,
  UserStatus,
} from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);

  // Users
  getUsers(page = 0, size = 20): Observable<PageResponse<UserResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<UserResponse>>(ApiConfig.users.list, { params });
  }

  getUserById(id: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(ApiConfig.users.byId(id));
  }

  getUsersByRole(role: UserRole, page = 0, size = 20): Observable<PageResponse<UserResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<UserResponse>>(ApiConfig.users.byRole(role), { params });
  }

  getUsersByStatus(status: UserStatus, page = 0, size = 20): Observable<PageResponse<UserResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<UserResponse>>(ApiConfig.users.byStatus(status), { params });
  }

  searchUsers(query: string, page = 0, size = 20): Observable<PageResponse<UserResponse>> {
    const params = new HttpParams().set('search', query).set('page', page).set('size', size);
    return this.http.get<PageResponse<UserResponse>>(ApiConfig.users.search, { params });
  }

  updateUser(id: string, request: UpdateUserRequest): Promise<UserResponse> {
    return firstValueFrom(
      this.http.put<UserResponse>(ApiConfig.users.byId(id), request),
    );
  }

  banUser(id: string): Promise<UserResponse> {
    return firstValueFrom(this.http.post<UserResponse>(ApiConfig.users.ban(id), null));
  }

  unbanUser(id: string): Promise<UserResponse> {
    return firstValueFrom(this.http.post<UserResponse>(ApiConfig.users.unban(id), null));
  }

  deleteUser(id: string): Promise<MessageResponse> {
    return firstValueFrom(this.http.delete<MessageResponse>(ApiConfig.users.byId(id)));
  }

  updatePerformanceScore(managerId: string, score: number): Promise<UserResponse> {
    const params = new HttpParams().set('score', score);
    return firstValueFrom(
      this.http.put<UserResponse>(ApiConfig.users.performanceScore(managerId), null, { params }),
    );
  }

  // Global notifications
  getAllNotifications(page = 0, size = 20): Observable<PageResponse<NotificationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<NotificationResponse>>(ApiConfig.notifications.list, { params });
  }

  // Global payments
  getAllPayments(page = 0, size = 20): Observable<PageResponse<PaymentResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<PaymentResponse>>(ApiConfig.payments.list, { params });
  }
}
