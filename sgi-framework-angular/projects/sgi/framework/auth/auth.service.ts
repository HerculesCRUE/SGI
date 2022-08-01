import { HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { hasAnyAuthority, hasAnyAuthorityForAnyUO, hasAnyModuleAccess, hasAuthority, hasAuthorityForAnyUO, hasModuleAccess } from './auth.authority';
import { SgiAuthConfig } from './auth.config';

export interface IAuthService {
  /**
   * Observable that emits authentication status changes. It's a event emiter, so the suscriber must take care about it.
   */
  readonly authStatus$: BehaviorSubject<IAuthStatus>;
  /**
   * Initialize the service. Must be called only one time during application initialization.
   * 
   * Returns true if the initialization succeeded
   */
  init(authConfig: SgiAuthConfig): Observable<boolean>;
  /**
   * Returns the JWT token that would be used to query protected service resources.
   */
  getToken(): Observable<string>;
  /**
   * Indicates if the current user is authenticated
   */
  isAuthenticated(): boolean;
  /**
   * Performs a redirect to the login page and after sucessfully login the user is redirected 
   * to the previous page or to the provided redirectUri.
   * 
   * @param redirectUri The uri to redirect after login. Must be relative
   */
  login(redirectUri?: string): Observable<void>;
  /**
   * Performs a logout of the current user. If redirectUri is provided, then the user is redirected 
   * to that uri after successfully logout.
   *
   * @param redirectUri The uri to redirect after logout. Must be relative
   */
  logout(redirectUri?: string): Observable<void>;
  /**
   * Returns true if the current authenticated user has any of the provided authorities
   * @param authorities Authorities to check
   */
  hasAnyAuthority(authorities: string[]): boolean;
  /**
   * Returns true if the current authenticated user has the provided authority
   * @param authority Authority to check
   */
  hasAuthority(authority: string): boolean;
  /**
   * Returns true if the current authenticated user has any of the provided authorities, ignoring the UO suffix.
   * @param authorities Authorities to check
   */
  hasAnyAuthorityForAnyUO(authorities: string[]): boolean;
  /**
   * Returns true if the current authenticated user has the provided authority, ignoring the UO suffix
   * @param authority Authority to check
   */
  hasAuthorityForAnyUO(authority: string): boolean;
  /**
   * Returns true if the current authenticated user has access to the provided module code
   * @param module Module code to check
   */
  hasModuleAccess(module: string): boolean;
  /**
   * Returns true if the current authenticated user has access to any of the provided module codes
   * @param modules Module codes to check
   */
  hasAnyModuleAccess(modules: string[]): boolean;
}

/**
 * Authentication status
 */
export interface IAuthStatus {
  /** Indicates if the user is authenticated */
  isAuthenticated: boolean;
  /** The authorities of the authenticated user */
  authorities: string[];
  /** Indicates if the user is considered as investigador entity */
  isInvestigador: boolean;
  /** The external user reference ID */
  userRefId: string;
  /** The modules access of the authenticated user */
  modules: string[];
  /** The preferred username to display */
  preferredUsername: string;
}

/**
 * Default authentication status where isn't an authenticated user
 */
export const defaultAuthStatus: IAuthStatus = {
  isAuthenticated: false,
  isInvestigador: false,
  authorities: [],
  userRefId: '',
  modules: [],
  preferredUsername: ''
};

/**
 * Base class for Auth services
 */
@Injectable()
export abstract class SgiAuthService implements IAuthService {

  readonly authStatus$ = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);

  protected get currentStatus(): IAuthStatus {
    return this.authStatus$.value;
  }

  protected protectedResources: RegExp[] = [];

  constructor() { }

  abstract init(authConfig: SgiAuthConfig): Observable<boolean>;
  abstract getToken(): Observable<string>;

  public isAuthenticated(): boolean {
    return this.currentStatus.isAuthenticated;
  }
  abstract login(redirectUri?: string): Observable<void>;
  abstract logout(redirectUri?: string): Observable<void>;

  public hasAnyAuthority(authorities: string[]): boolean {
    if (!this.isAuthenticated() || !authorities || !authorities.length) {
      return false;
    }
    if (!this.currentStatus.authorities || !this.currentStatus.authorities.length) {
      return false;
    }
    return hasAnyAuthority(this.currentStatus.authorities, authorities);
  }

  public hasAuthority(authority: string): boolean {
    if (!this.isAuthenticated() || !authority) {
      return false;
    }
    if (!this.currentStatus.authorities || !this.currentStatus.authorities.length) {
      return false;
    }
    return hasAuthority(this.authStatus$.value.authorities, authority);
  }

  public hasAnyAuthorityForAnyUO(authorities: string[]): boolean {
    if (!this.isAuthenticated() || !authorities || !authorities.length) {
      return false;
    }
    if (!this.currentStatus.authorities || !this.currentStatus.authorities.length) {
      return false;
    }
    return hasAnyAuthorityForAnyUO(this.currentStatus.authorities, authorities);
  }

  public hasAuthorityForAnyUO(authority: string): boolean {
    if (!this.isAuthenticated() || !authority) {
      return false;
    }
    if (!this.currentStatus.authorities || !this.currentStatus.authorities.length) {
      return false;
    }
    return hasAuthorityForAnyUO(this.currentStatus.authorities, authority);
  }

  public hasModuleAccess(module: string): boolean {
    if (!this.isAuthenticated() || !module) {
      return false;
    }
    if (!this.currentStatus.modules || !this.currentStatus.modules.length) {
      return false;
    }
    return hasModuleAccess(this.currentStatus.modules, module);
  }

  public hasAnyModuleAccess(modules: string[]): boolean {
    if (!this.isAuthenticated() || !modules || !modules.length) {
      return false;
    }
    if (!this.currentStatus.modules || !this.currentStatus.modules.length) {
      return false;
    }
    return hasAnyModuleAccess(this.currentStatus.modules, modules);
  }

  public isProtectedRequest(request: HttpRequest<any>): boolean {
    return this.protectedResources.findIndex((regex) => regex.test(request.url)) !== -1;
  }
}
