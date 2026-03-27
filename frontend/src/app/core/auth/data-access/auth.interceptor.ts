import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ApiConfig } from '../../config/api.config';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

const PUBLIC_URLS = [
  ApiConfig.auth.login,
  ApiConfig.auth.register,
  ApiConfig.auth.refresh,
];

function isPublicUrl(url: string): boolean {
  return PUBLIC_URLS.some((publicUrl) => url.startsWith(publicUrl));
}

function addToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
}

function handleTokenRefresh(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  router: Router,
): Observable<any> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return new Observable((subscriber) => {
      authService
        .refreshToken()
        .then((response) => {
          isRefreshing = false;
          refreshTokenSubject.next(response.accessToken);
          next(addToken(req, response.accessToken)).subscribe(subscriber);
        })
        .catch((err) => {
          isRefreshing = false;
          refreshTokenSubject.next(null);
          authService.logout();
          subscriber.error(err);
        });
    });
  }

  // Another refresh is in progress — wait for it
  return refreshTokenSubject.pipe(
    filter((token) => token !== null),
    take(1),
    switchMap((token) => next(addToken(req, token!))),
  );
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (isPublicUrl(req.url)) {
    return next(req);
  }

  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getAccessToken();

  const authedReq = token ? addToken(req, token) : req;

  return next(authedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isPublicUrl(req.url)) {
        return handleTokenRefresh(req, next, authService, router);
      }
      return throwError(() => error);
    }),
  );
};
