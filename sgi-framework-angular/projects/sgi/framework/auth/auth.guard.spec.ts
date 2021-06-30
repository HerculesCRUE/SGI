import { SgiAuthGuard } from './auth.guard';

import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { of, Observable, throwError } from 'rxjs';
import { SgiAuthRouteData, SgiAuthRoute } from './auth.route';
import { SgiAuthService } from './auth.service';

describe('CanAuthenticationGuard', () => {
  let guard: SgiAuthGuard;
  let authService: jasmine.SpyObj<SgiAuthService>;
  let routeMock: ActivatedRouteSnapshot = {
    pathFromRoot: [],
    routeConfig: {

    } as SgiAuthRoute
  } as ActivatedRouteSnapshot;
  const routeStateMock: any = { snapshot: {}, url: '/cookies' };

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'login', 'hasAuthority', 'hasAnyAuthority', 'hasAuthorityForAnyUO', 'hasAnyAuthorityForAnyUO']);
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        { provide: Router, useValue: routeMock },
        { provide: SgiAuthService, useValue: authServiceSpy }
      ]
    });
    authService = TestBed.inject(SgiAuthService) as any;
    guard = TestBed.inject(SgiAuthGuard);
  });

  afterEach(() => {
    // Clean route
    routeMock = {
      // This attribute is mandatory
      pathFromRoot: [],
      routeConfig: {

      } as SgiAuthRoute
    } as ActivatedRouteSnapshot;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true and login must be called if not authenticated ', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(false);
    authService.login.and.returnValue(of());

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
    // Check auth login call
    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('should return false if not authenticated and login call throws error', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(false);
    authService.login.and.returnValue(throwError('ERROR'));

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
    // Check auth login call
    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('should return true if authenticated and no auth route data', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAuthority.and.returnValue(false);

    // Route data
    routeMock.routeConfig.data = {
      hasAuthority: 'FOUR',
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAuthority.and.returnValue(true);

    // Route data
    routeMock.routeConfig.data = {
      hasAuthority: 'TWO'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAnyAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyAuthority.and.returnValue(false);

    // Route data
    routeMock.routeConfig.data = {
      hasAnyAuthority: ['FOUR', 'FIVE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAnyAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyAuthority.and.returnValue(true);

    // Route data
    routeMock.routeConfig.data = {
      hasAnyAuthority: ['FOUR', 'THREE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAuthorityForAnyUO.and.returnValue(false);

    // Route data
    routeMock.routeConfig.data = {
      hasAuthorityForAnyUO: 'FIVE'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAuthorityForAnyUO.and.returnValue(true);

    // Route data
    routeMock.routeConfig.data = {
      hasAuthorityForAnyUO: 'FOUR'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAnyAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyAuthorityForAnyUO.and.returnValue(false);

    // Route data
    routeMock.routeConfig.data = {
      hasAnyAuthorityForAnyUO: ['SIX', 'FIVE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAnyAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.hasAnyAuthorityForAnyUO.and.returnValue(true);

    // Route data
    routeMock.routeConfig.data = {
      hasAnyAuthorityForAnyUO: ['FIVE', 'FOUR']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with mixed auth data attributes', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);

    // Route data
    routeMock.routeConfig.data = {
      hasAuthority: 'ONE',
      hasAnyAuthorityForAnyUO: ['TWO', 'THREE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });
});
