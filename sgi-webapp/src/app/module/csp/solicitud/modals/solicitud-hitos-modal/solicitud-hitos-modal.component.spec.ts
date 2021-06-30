import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SolicitiudHitosModalComponent } from './solicitud-hitos-modal.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import TestUtils from '@core/utils/test-utils';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';


describe('SolicitiudHitosModalComponent', () => {
  let component: SolicitiudHitosModalComponent;
  let fixture: ComponentFixture<SolicitiudHitosModalComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };
    // Mock MAT_DIALOG
    const matDialogData = {} as ISolicitudHito;

    TestBed.configureTestingModule({
      declarations: [
        SolicitiudHitosModalComponent,
        DialogComponent,
        HeaderComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitiudHitosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
