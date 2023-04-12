import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EtiSharedModule } from 'src/app/module/eti/shared/eti-shared.module';
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
    evaluacion: {
      convocatoriaReunion: { fechaEvaluacion: DateTime.now() } as IConvocatoriaReunion
    } as IEvaluacion
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
        SgiAuthModule,
        SharedModule,
        EtiSharedModule
      ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
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
