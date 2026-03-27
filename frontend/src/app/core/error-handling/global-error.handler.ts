import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../../shared/data-access/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 is handled by the auth interceptor
      if (error.status === 401) {
        return throwError(() => error);
      }

      switch (error.status) {
        case 400:
          notificationService.showError(error.error?.message || 'Invalid request');
          break;
        case 403:
          notificationService.showError('You do not have permission for this action');
          break;
        case 404:
          notificationService.showError('Resource not found');
          break;
        case 409:
          notificationService.showError(error.error?.message || 'Conflict detected');
          break;
        case 0:
          notificationService.showError('Network error. Please check your connection.');
          break;
        default:
          if (error.status >= 500) {
            notificationService.showError('Server error. Please try again later.');
          }
          break;
      }

      return throwError(() => error);
    }),
  );
};
