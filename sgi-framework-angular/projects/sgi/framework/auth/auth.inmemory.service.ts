import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { SgiAuthConfig } from './auth.config';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from './auth.service';

/**
 * InMemory authentication service implementation
 */
@Injectable()
export class AuthInMemoryService extends SgiAuthService {

  private authenticated: boolean;
  private authorities: string[];
  private defaultAuthStatus: IAuthStatus;

  constructor(private router: Router) {
    super();
  }

  init(authConfig: SgiAuthConfig): Observable<boolean> {
    this.defaultAuthStatus = authConfig.inMemoryConfig;
    this.authenticated = false;
    return of(this.authenticated);
  }
  getToken(): Observable<string> {
    return of('');
  }
  isAuthenticated(): boolean {
    return this.authenticated;
  }
  login(redirectUri?: string): Observable<void> {
    this.authenticated = true;
    this.authorities = this.defaultAuthStatus.authorities;
    this.authStatus$.next(this.getAuthStatus());
    if (redirectUri) {
      this.router.navigate([redirectUri]);
    }
    return of();
  }
  logout(redirectUri?: string): Observable<void> {
    this.authenticated = false;
    this.authStatus$.next(defaultAuthStatus);
    if (redirectUri) {
      this.router.navigate([redirectUri]);
    }
    return of();
  }

  private getAuthStatus(): IAuthStatus {
    return {
      isAuthenticated: this.authenticated,
      isInvestigador: this.defaultAuthStatus.isInvestigador,
      authorities: this.defaultAuthStatus.authorities,
      userRefId: this.defaultAuthStatus.userRefId,
      modules: this.defaultAuthStatus.modules,
      preferredUsername: this.defaultAuthStatus.preferredUsername
    };
  }
}
