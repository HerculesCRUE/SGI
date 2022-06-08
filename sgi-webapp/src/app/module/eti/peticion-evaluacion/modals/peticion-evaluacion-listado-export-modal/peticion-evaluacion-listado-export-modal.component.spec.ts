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
import { PeticionEvaluacionAsignacionTareasListadoExportService } from '../../peticion-evaluacion-asignacion-tareas-listado-export.service';
import { PeticionEvaluacionEquipoInvestigadorListadoExportService } from '../../peticion-evaluacion-equipo-investigador-listado-export.service';
import { PeticionEvaluacionGeneralListadoExportService } from '../../peticion-evaluacion-general-listado-export.service';
import { PeticionEvaluacionListadoExportService } from '../../peticion-evaluacion-listado-export.service';
import { PeticionEvaluacionMemoriasListadoExportService } from '../../peticion-evaluacion-memorias-listado-export.service';
import { IPeticionEvaluacionListadoModalData, PeticionEvaluacionListadoExportModalComponent } from './peticion-evaluacion-listado-export-modal.component';
describe('PeticionEvaluacionListadoExportModalComponent', () => {
  let component: PeticionEvaluacionListadoExportModalComponent;
  let fixture: ComponentFixture<PeticionEvaluacionListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PeticionEvaluacionListadoExportModalComponent
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
        { provide: MAT_DIALOG_DATA, useValue: {} as IPeticionEvaluacionListadoModalData },
        SgiAuthService,
        PeticionEvaluacionListadoExportService,
        PeticionEvaluacionGeneralListadoExportService,
        PeticionEvaluacionEquipoInvestigadorListadoExportService,
        PeticionEvaluacionAsignacionTareasListadoExportService,
        PeticionEvaluacionMemoriasListadoExportService,
        LuxonDatePipe
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
