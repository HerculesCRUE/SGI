import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaReunionAsignacionMemoriasModalComponent, ConvocatoriaReunionAsignacionMemoriasModalComponentData } from './convocatoria-reunion-asignacion-memorias-modal.component';

describe('ConvocatoriaReunionAsignacionMemoriasModalComponent', () => {
  let component: ConvocatoriaReunionAsignacionMemoriasModalComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionAsignacionMemoriasModalComponent>;

  const dialogData = {
    idConvocatoria: 0,
    memoriasAsignadas: [] as IMemoria[],
    filterMemoriasAsignables: {
      idComite: 0,
      idTipoConvocatoria: 0,
      fechaLimite: DateTime.now()
    },
    evaluacion: {} as IEvaluacion
  } as ConvocatoriaReunionAsignacionMemoriasModalComponentData;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionAsignacionMemoriasModalComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        RouterTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        { provide: MatDialogRef, useValue: dialogData },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionAsignacionMemoriasModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
