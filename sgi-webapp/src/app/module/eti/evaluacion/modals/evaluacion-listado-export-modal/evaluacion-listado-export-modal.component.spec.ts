import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EvaluacionEvaluacionesAnterioresListadoExportService } from '../../evaluacion-evaluaciones-anteriores-listado-export.service';
import { EvaluacionGeneralListadoExportService } from '../../evaluacion-general-listado-export.service';
import { EvaluacionListadoExportService } from '../../evaluacion-listado-export.service';
import { EvaluacionListadoExportModalComponent, IEvaluacionListadoModalData } from './evaluacion-listado-export-modal.component';

describe('EvaluacionListadoExportModalComponent', () => {
  let component: EvaluacionListadoExportModalComponent;
  let fixture: ComponentFixture<EvaluacionListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionListadoExportModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IEvaluacionListadoModalData },
        SgiAuthService,
        EvaluacionListadoExportService,
        EvaluacionGeneralListadoExportService,
        EvaluacionEvaluacionesAnterioresListadoExportService,
        LuxonDatePipe
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
