import { Component, Injectable } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { defaultAuthStatus, IAuthStatus, SgiAuthService } from '../auth.service';
import { SgiAuthConfig } from '../public-api';
import { HasModuleAccessDirective } from './has-module-access.directive';

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

function createAuthStatus(authenticated: boolean, modules: string[]): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.isAuthenticated = authenticated;
  authStatus.modules = modules;
  return authStatus;
}

describe('HasModuleAccessDirective', () => {
  let mockAuthService: MockAuthService;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [TestComponent, HasModuleAccessDirective],
      providers: [{ provide: SgiAuthService, useClass: MockAuthService }]
    });
  });

  it('With authStatus with module GEN, then should be rendered', () => {
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    const userModules = ['GEN'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without module GEN, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    const userModules = ['LOCK'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change module, then should be rendered', () => {
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    const userModules = [''];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    userModules.push('GEN');
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change module, then shouldn\'t be rendered', () => {
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    const userModules = ['GEN'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    userModules.pop();
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change module, then shouldn\'t duplicate element', () => {
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    const userModules = ['GEN'];

    fixture = createTestComponent(template);
    mockAuthService = TestBed.inject(SgiAuthService) as any;
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    userModules.push('MASTER');
    mockAuthService.setAuthStatus(createAuthStatus(true, userModules));

    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty module error will be throw', () => {
    const template = `<div *sgiHasModuleAccess="''">Test with empty module</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });

  it('With no type string module name error will be throw', () => {
    const template = `<div *sgiHasModuleAccess="1">Test with module: 1</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });

  it('With no module value error will be throw', () => {
    const template = `<div *sgiHasModuleAccess>Test without module value</div>`;

    fixture = createTestComponent(template);

    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });
});
