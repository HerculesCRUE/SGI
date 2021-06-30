import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { BreadcrumbComponent } from '@shared/breadcrumb/breadcrumb.component';
import { RootComponent } from '@shared/root/root.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { InvMenuPrincipalComponent } from '../inv-menu-principal/inv-menu-principal.component';
import { InvRootComponent } from './inv-root.component';

describe('InvRootComponent', () => {
  let component: InvRootComponent;
  let fixture: ComponentFixture<InvRootComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InvRootComponent,
        RootComponent,
        InvMenuPrincipalComponent,
        BreadcrumbComponent
      ],
      imports: [
        BrowserAnimationsModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule.withRoutes([]),
        SgiAuthModule,
        HttpClientTestingModule
      ],
      providers: [
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
