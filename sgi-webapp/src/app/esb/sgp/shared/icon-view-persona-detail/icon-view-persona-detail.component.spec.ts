import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IconViewPersonaDetailComponent } from './icon-view-persona-detail.component';

describe('IconViewPersonaDetailComponent', () => {
  let component: IconViewPersonaDetailComponent;
  let fixture: ComponentFixture<IconViewPersonaDetailComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData = {};

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        ReactiveFormsModule,
        SgiAuthModule,
      ],
      providers: [

        SgiAuthService,
        {
          provide: MatDialogRef,
          useValue: TestUtils.buildDialogCommonMatDialogRef(),
        },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
      ],
      declarations: [IconViewPersonaDetailComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IconViewPersonaDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
