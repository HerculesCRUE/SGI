import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class SgiLanguageHttpInterceptor implements HttpInterceptor {

  constructor(@Inject(LOCALE_ID) private locale: string) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authRequest = req.clone({ setHeaders: { 'Accept-Language': this.locale } });
    return next.handle(authRequest);
  }
}
