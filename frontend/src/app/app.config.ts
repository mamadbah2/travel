import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { routes } from './app.routes';
import { authInterceptor } from './core/auth/data-access/auth.interceptor';
import { errorInterceptor } from './core/error-handling/global-error.handler';

const interceptors = [authInterceptor, errorInterceptor];

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
