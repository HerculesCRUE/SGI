import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IPartidaGasto } from '@core/models/csp/partida-gasto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../csp-shared.module';
import { PartidaGastoDataModal, PartidaGastoModalComponent } from './partida-gasto-modal.component';

describe('PartidaGastoModalComponent', () => {
  let component: PartidaGastoModalComponent;
  let fixture: ComponentFixture<PartidaGastoModalComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };
    // Mock MAT_DIALOG
    const matDialogData = {
      partidaGasto: {
        conceptoGasto: {} as IConceptoGasto
      } as IPartidaGasto
    } as PartidaGastoDataModal;

    TestBed.configureTestingModule({
      declarations: [
        PartidaGastoModalComponent,
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
    fixture = TestBed.createComponent(PartidaGastoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
