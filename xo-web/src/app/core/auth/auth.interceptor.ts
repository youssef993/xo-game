import { inject } from '@angular/core';
import {
  HttpInterceptorFn
} from '@angular/common/http';
import { from, switchMap } from 'rxjs';

import { AuthService } from './auth.service';

const API_URLS = [
  'http://localhost:8080',
  'http://localhost:8082',
  'http://localhost:8081',
  'http://localhost:8083'
];

export const authInterceptor: HttpInterceptorFn =
  (request, next) => {

    const authService = inject(AuthService);

    const protectedRequest = API_URLS.some(
      apiUrl => request.url.startsWith(apiUrl)
    );

    if (!protectedRequest) {
      return next(request);
    }

    return from(authService.getValidToken()).pipe(
      switchMap(token => {
        if (!token) {
          return next(request);
        }

        return next(
          request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          })
        );
      })
    );
  };
