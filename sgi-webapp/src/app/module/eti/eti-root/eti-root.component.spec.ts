import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { BreadcrumbComponent } from '@shared/breadcrumb/breadcrumb.component';
import { RootComponent } from '@shared/root/root.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EtiRootComponent } from './eti-root.component';

describe('EtiRootComponent', () => {
  let component: EtiRootComponent;
  let fixture: ComponentFixture<EtiRootComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EtiRootComponent,
        RootComponent,
        BreadcrumbComponent
      ],
      imports: [
        BrowserAnimationsModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule.withRoutes([]),
        SgiAuthModule,
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EtiRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
