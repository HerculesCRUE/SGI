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
import { PubRootComponent } from './pub-root.component';

describe('PubRootComponent', () => {
  let component: PubRootComponent;
  let fixture: ComponentFixture<PubRootComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PubRootComponent,
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
        HttpClientTestingModule
      ],
      providers: [
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PubRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
