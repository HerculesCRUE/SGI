import { Component, Injectable } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from '../auth.service';
import { SgiAuthConfig } from '../public-api';
import { HasAuthorityDirective } from './has-authority.directive';

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

function createAuthStatus(authenticated: boolean, authorities: string[]): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.isAuthenticated = authenticated;
  authStatus.authorities = authorities;
  return authStatus;
}


describe('HasAuthorityDirective', () => {
  let mockAuthService: MockAuthService;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [TestComponent, HasAuthorityDirective],
      providers: [{ provide: SgiAuthService, useClass: MockAuthService }]
    });
  });


  it('With authStatus with authority VIEW, then should be rendered', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = ['VIEW'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without authority VIEW, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = [''];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('With authStatus with authority VIEW_UO, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = ['VIEW_UO'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change authority, then should be rendered', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = [''];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    userAuthorities.push('VIEW');
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change authority, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = ['VIEW'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    userAuthorities.pop();
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change authority, then shouldn\'t duplicate element', () => {
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    const userAuthorities = ['VIEW'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    userAuthorities.push('UPDATE');
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty authority error will be throw', () => {
    const template = `<div *sgiHasAuthority="''">Test with empty authority</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no type string authority error will be throw', () => {
    const template = `<div *sgiHasAuthority="1">Test with authority: 1</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no authority value error will be throw', () => {
    const template = `<div *sgiHasAuthority>Test without authority value</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });
});



