import { Component, Injectable } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { SgiAuthConfig } from '../auth.config';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from '../auth.service';
import { HasAnyAuthorityForAnyUODirective } from './has-any-authority-for-any-uo.directive';

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

describe('HasAnyAuthorityForAnyUODirective', () => {
  let mockAuthService: MockAuthService;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [TestComponent, HasAnyAuthorityForAnyUODirective],
      providers: [{ provide: SgiAuthService, useClass: MockAuthService }]
    });
  });

  it('With authStatus with authority VIEW_UO, then should be rendered', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    const userAuthorities = ['VIEW_UO'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without authority VIEW_UO, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    const userAuthorities = [''];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change authority, then should be rendered', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    const userAuthorities = [''];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    userAuthorities.push('VIEW_UO');
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change authority, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
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
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    const userAuthorities = ['VIEW_UO'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    userAuthorities.push('UPDATE_UO');
    mockAuthService.setAuthStatus(createAuthStatus(true, userAuthorities));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty authority error will be throw', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW','']">Test with empty authority in authorities</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no type string array authorities error will be throw', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="1">Test with authorities: 1</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an array of authorities'));
  });

  it('With authorities that contains UO error will be throw', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'VIEW_UO']">Test with authorities: VIEW and VIEW_UO</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Authority cannot contain an underscore'));
  });

  it('With no authorities value error will be throw', () => {
    const template = `<div *sgiHasAnyAuthorityForAnyUO>Test without authorities value</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an array of authorities'));
  });
});
