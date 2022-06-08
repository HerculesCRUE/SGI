import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EvaluadorConflictosInteresListadoExportService } from '../../evaluador-conflictos-interes-listado-export.service';
import { EvaluadorGeneralListadoExportService } from '../../evaluador-general-listado-export.service';
import { EvaluadorListadoExportService } from '../../evaluador-listado-export.service';
import { EstadoEvaluadorPipe } from '../../pipes/estado-evaluador.pipe';
import { EvaluadorListadoExportModalComponent, IEvaluadorListadoModalData } from './evaluador-listado-export-modal.component';

describe('EvaluadorListadoExportModalComponent', () => {
  let component: EvaluadorListadoExportModalComponent;
  let fixture: ComponentFixture<EvaluadorListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluadorListadoExportModalComponent,
        EstadoEvaluadorPipe
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
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IEvaluadorListadoModalData },
        SgiAuthService,
        EvaluadorListadoExportService,
        EvaluadorGeneralListadoExportService,
        EvaluadorConflictosInteresListadoExportService,
        LuxonDatePipe,
        EstadoEvaluadorPipe
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluadorListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
