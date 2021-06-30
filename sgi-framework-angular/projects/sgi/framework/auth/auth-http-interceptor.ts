import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { SgiAuthService } from './auth.service';


/**
 * Interceptor that injects authorization header with the JWT token.
 *
 * If the token cannot be obtained from the AuthService, and the user isn't authenticated or the session is expired,
 * then the request is aborted and the user is redirected to login page
 */
@Injectable()
export class SgiAuthHttpInterceptor implements HttpInterceptor {

  constructor(private authService: SgiAuthService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.authService.isProtectedRequest(req)) {
      return this.authService.getToken().pipe(
        catchError((error) => {
          console.error(JSON.stringify(error));
          return undefined as string;
        }),
        switchMap((token) => {
          if (token) {
            const authRequest = req.clone({ setHeaders: { authorization: `Bearer ${token}` } });
            return next.handle(authRequest);
          }
          else {
            return next.handle(req);
          }
        })
      );
    }
    else {
      return next.handle(req);
    }
  }
}
