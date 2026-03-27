import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { routes } from './app.routes';
import { mockInterceptor } from './core/mock/mock.interceptor';
import { authInterceptor } from './core/auth/data-access/auth.interceptor';
import { errorInterceptor } from './core/error-handling/global-error.handler';
import { environment } from '../environments/environment';

const interceptors = environment.production
  ? [authInterceptor, errorInterceptor]
  : [mockInterceptor, authInterceptor, errorInterceptor];

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(
      withInterceptors(interceptors),
      withFetch(),
    ),
    provideClientHydration(withEventReplay()),
  ],
};
