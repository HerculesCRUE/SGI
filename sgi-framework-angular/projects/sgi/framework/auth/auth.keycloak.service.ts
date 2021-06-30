import { PlatformLocation } from '@angular/common';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import * as Keycloak_ from 'keycloak-js';
import { from, Observable, of, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { extractModuleAccess } from './auth.authority';
import { SgiAuthConfig } from './auth.config';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from './auth.service';
export const Keycloak = Keycloak_;

interface IKeycloakToken extends Keycloak.KeycloakTokenParsed {
  preferred_username: string;
  investigador: boolean;
  user_ref_id: string;
}

/**
 * Keycloak authentication service implementation.
 */
@Injectable()
export class AuthKeycloakService extends SgiAuthService {

  private keycloak: Keycloak.KeycloakInstance;
  constructor(private router: Router, private platformLocation: PlatformLocation) {
    super();
  }

  public init(authConfig: SgiAuthConfig): Observable<boolean> {
    this.keycloak = Keycloak({
      clientId: authConfig.ssoClientId,
      realm: authConfig.ssoRealm,
      url: authConfig.ssoUrl
    });
    this.bindEvents();
    this.protectedResources = authConfig.protectedResources;
    let initOptions: Keycloak.KeycloakInitOptions;
    initOptions = {
      enableLogging: true
    };

    return from(this.keycloak.init(initOptions));
  }

  private bindEvents(): void {
    this.keycloak.onActionUpdate = (status) => {
    };
    this.keycloak.onAuthError = (errorData) => {
    };
    this.keycloak.onAuthLogout = () => {
      // Called automatically if cannot refresh token or after invoking logout or when session expires
      this.publishAuthStatus(defaultAuthStatus);
    };
    this.keycloak.onAuthRefreshError = () => {
      // Called when failed the refreshing token. If it is expired, onAuthLogout is called first
      this.publishAuthStatus(defaultAuthStatus);
    };
    this.keycloak.onAuthRefreshSuccess = () => {
      // Called when token is refreshed successfully.
    };
    this.keycloak.onAuthSuccess = () => {
      // Called after successfull login, or when a valid session exists during init
      this.publishAuthStatus(this.getAuthStatus());
    };
    this.keycloak.onReady = (authenticated) => {
      // Called after initialization of the adapter. If authenticated, onAuthSuccess is called first
      if (authenticated && this.keycloak.isTokenExpired(5)) {
        this.refreshToken().subscribe(
          () => this.publishAuthStatus(this.getAuthStatus()),
          () => this.publishAuthStatus(defaultAuthStatus)
        );
      }
      else if (!authenticated) {
        this.publishAuthStatus(defaultAuthStatus);
      }
      else {
        this.publishAuthStatus(this.getAuthStatus());
      }
    };
  }

  public login(redirectUri?: string): Observable<void> {
    let loginOptions: Keycloak.KeycloakLoginOptions;
    if (redirectUri) {
      let baseUrl = `${this.platformLocation.protocol}//${this.platformLocation.hostname}`;
      if (this.platformLocation.port) {
        baseUrl += `:${this.platformLocation.port}`;
      }
      loginOptions = {
        redirectUri: baseUrl + redirectUri
      };
    }
    return from(this.keycloak.login(loginOptions));
  }

  public logout(redirectUri?: string): Observable<void> {
    let logoutOptios: Keycloak.KeycloakLogoutOptions;
    if (redirectUri) {
      let baseUrl = `${this.platformLocation.protocol}//${this.platformLocation.hostname}`;
      if (this.platformLocation.port) {
        baseUrl += `:${this.platformLocation.port}`;
      }
      logoutOptios = {
        redirectUri: baseUrl + redirectUri
      };
    }
    return from(this.keycloak.logout(logoutOptios));
  }

  private get decodedToken(): IKeycloakToken {
    return this.keycloak.tokenParsed as IKeycloakToken;
  }

  private getAuthStatus(): IAuthStatus {
    const token: IKeycloakToken = this.decodedToken;
    return {
      isInvestigador: token.investigador,
      isAuthenticated: this.keycloak.authenticated,
      userRefId: token.user_ref_id,
      authorities: token.realm_access?.roles ? token.realm_access.roles : [],
      modules: token.realm_access?.roles ? extractModuleAccess(token.realm_access?.roles) : []
    };
  }

  private refreshToken(): Observable<boolean> {
    return from(this.keycloak.updateToken(5));
  }

  public getToken(): Observable<string> {
    let expired: boolean;
    try {
      expired = this.keycloak.isTokenExpired(5);
    }
    catch (e) {
      return throwError(e);
    }
    if (expired) {
      return this.refreshToken().pipe(
        map(() => this.keycloak.token)
      );
    }
    return of(this.keycloak.token);
  }

  private publishAuthStatus(status: IAuthStatus) {
    this.authStatus$.next(status);
  }
}
