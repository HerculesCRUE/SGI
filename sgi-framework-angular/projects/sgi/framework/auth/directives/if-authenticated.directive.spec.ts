import { Component, Injectable } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from '../auth.service';
import { SgiAuthConfig } from '../public-api';
import { IfAuthenticatedDirective } from './if-authenticated.directive';

@Component({
  template: '',
})
class TestComponent {
}

@Injectable()
class MockAuthService extends SgiAuthService {
  init(authConfig: SgiAuthConfig): Observable<boolean> {
    throw new Error('Method not implemented.');
  }
  getToken(): Observable<string> {
    throw new Error('Method not implemented.');
  }
  login(redirectUri?: string): Observable<void> {
    throw new Error('Method not implemented.');
  }
  logout(redirectUri?: string): Observable<void> {
    throw new Error('Method not implemented.');
  }
  setAuthStatus(authStatus: IAuthStatus) {
    this.authStatus$.next(authStatus);
  }
}

function createTestComponent(template: string): ComponentFixture<TestComponent> {
  return TestBed.overrideComponent(TestComponent, { set: { template } })
    .createComponent(TestComponent);
}

function createAuthStatus(authenticated: boolean): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.isAuthenticated = authenticated;
  return authStatus;
}

describe('IfAuthenticatedDirective', () => {
  let mockAuthService: MockAuthService;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [TestComponent, IfAuthenticatedDirective],
      providers: [{ provide: SgiAuthService, useClass: MockAuthService }]
    });
  });


  it('With authStatus authenticated, then should be rendered', () => {
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus unauthenticated, then shouldn\'t be rendered', () => {
    const template = `<div *sgiIfAuthenticated>Test with unauthenticated</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(false));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change to authenticated, then should be rendered', () => {
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(false));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change to unauthenticated, then shouldn\'t be rendered', () => {
    const template = `<div *sgiIfAuthenticated>Test with unauthenticated</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    mockAuthService.setAuthStatus(createAuthStatus(false));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change, then shouldn\'t duplicate element', () => {
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authState authenticated and attribute value, shouldn\'t throw a error and will be rendered', () => {
    const template = `<div *sgiIfAuthenticated="'DA'">Test with attribute value</div>`;

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });
});
