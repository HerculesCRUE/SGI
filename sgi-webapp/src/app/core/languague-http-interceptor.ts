import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LanguageService } from './services/language.service';

@Injectable()
export class SgiLanguageHttpInterceptor implements HttpInterceptor {

  constructor(private languageService: LanguageService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authRequest = req.clone({ setHeaders: { 'Accept-Language': this.languageService.getLanguage().code } });
    return next.handle(authRequest);
  }
}
