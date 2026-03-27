import { Routes } from '@angular/router';
import { LayoutShellComponent } from './core/layout/ui/layout-shell/layout-shell.component';
import { authGuard } from './core/auth/guards/auth.guard';
import { roleGuard } from './core/auth/guards/role.guard';

export const routes: Routes = [
  // Auth pages — full screen, no layout shell
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },

  // All other pages — wrapped in layout shell (navbar + toast)
  {
    path: '',
    component: LayoutShellComponent,
    children: [
      {
        path: '',
        loadChildren: () => import('./features/home/home.routes').then((m) => m.HOME_ROUTES),
      },
      {
        path: 'travels',
        loadChildren: () =>
          import('./features/travels/travels.routes').then((m) => m.TRAVEL_ROUTES),
      },
      {
        path: 'search',
        loadChildren: () =>
          import('./features/search/search.routes').then((m) => m.SEARCH_ROUTES),
      },
      {
        path: 'subscriptions',
        canActivate: [authGuard, roleGuard],
        data: { roles: ['TRAVELER'] },
        loadChildren: () =>
          import('./features/subscriptions/subscriptions.routes').then(
            (m) => m.SUBSCRIPTION_ROUTES,
          ),
      },
      {
        path: 'payments',
        canActivate: [authGuard],
        loadChildren: () =>
          import('./features/payments/payments.routes').then((m) => m.PAYMENT_ROUTES),
      },
      {
        path: 'notifications',
        canActivate: [authGuard],
        loadChildren: () =>
          import('./features/notifications/notifications.routes').then(
            (m) => m.NOTIFICATION_ROUTES,
          ),
      },
      {
        path: 'profile',
        canActivate: [authGuard],
        loadChildren: () =>
          import('./features/user/user.routes').then((m) => m.USER_ROUTES),
      },
      {
        path: 'recommendations',
        canActivate: [authGuard, roleGuard],
        data: { roles: ['TRAVELER'] },
        loadChildren: () =>
          import('./features/recommendations/recommendations.routes').then(
            (m) => m.RECOMMENDATION_ROUTES,
          ),
      },
      {
        path: 'manager',
        canActivate: [authGuard, roleGuard],
        data: { roles: ['MANAGER'] },
        loadChildren: () =>
          import('./features/manager/manager.routes').then((m) => m.MANAGER_ROUTES),
      },
      {
        path: 'admin',
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () =>
          import('./features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
      },
      {
        path: 'unauthorized',
        loadComponent: () =>
          import('./features/unauthorized/unauthorized.component').then(
            (m) => m.UnauthorizedComponent,
          ),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
