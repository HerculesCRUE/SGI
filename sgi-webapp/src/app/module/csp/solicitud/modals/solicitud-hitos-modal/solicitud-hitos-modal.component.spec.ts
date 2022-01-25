import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { SolicitiudHitosModalComponent } from './solicitud-hitos-modal.component';



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
        SgiAuthModule,
        SharedModule,
        CspSharedModule
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
