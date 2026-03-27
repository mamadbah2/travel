import { Routes } from '@angular/router';
import { AdminDashboardPageComponent } from './features/admin-dashboard-page/admin-dashboard-page.component';
import { AdminUsersPageComponent } from './features/admin-users-page/admin-users-page.component';
import { AdminUserDetailPageComponent } from './features/admin-user-detail-page/admin-user-detail-page.component';
import { AdminPaymentsPageComponent } from './features/admin-payments-page/admin-payments-page.component';
import { AdminNotificationsPageComponent } from './features/admin-notifications-page/admin-notifications-page.component';
import { AdminReportsPageComponent } from '../../features/reports/features/admin-reports-page/admin-reports-page.component';
import { AdminAnalyticsPageComponent } from './features/admin-analytics-page/admin-analytics-page.component';

export const ADMIN_ROUTES: Routes = [
  { path: '', component: AdminDashboardPageComponent },
  { path: 'users', component: AdminUsersPageComponent },
  { path: 'users/:userId', component: AdminUserDetailPageComponent },
  { path: 'payments', component: AdminPaymentsPageComponent },
  { path: 'notifications', component: AdminNotificationsPageComponent },
  { path: 'reports', component: AdminReportsPageComponent },
  { path: 'analytics', component: AdminAnalyticsPageComponent },
];
