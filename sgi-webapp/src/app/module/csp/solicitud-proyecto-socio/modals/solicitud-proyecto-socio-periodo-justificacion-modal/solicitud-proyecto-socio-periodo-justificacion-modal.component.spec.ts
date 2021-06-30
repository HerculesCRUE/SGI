import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioPeriodoJustificacionModalComponent, SolicitudProyectoSocioPeriodoJustificacionModalData } from './solicitud-proyecto-socio-periodo-justificacion-modal.component';

describe('SolicitudProyectoSocioPeriodoJustificacionesModalComponent', () => {
  let component: SolicitudProyectoSocioPeriodoJustificacionModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioPeriodoJustificacionModalComponent>;
  const periodoJustificacion: ISolicitudProyectoSocioPeriodoJustificacion = {
    fechaFin: undefined,
    fechaInicio: undefined,
    id: undefined,
    mesFinal: undefined,
    mesInicial: undefined,
    numPeriodo: undefined,
    observaciones: undefined,
    solicitudProyectoSocioId: undefined
  };

  const newData: SolicitudProyectoSocioPeriodoJustificacionModalData = {
    isEdit: false,
    periodoJustificacion,
    selectedPeriodoJustificaciones: [],
    mesFinSolicitudProyectoSocio: 2,
    mesInicioSolicitudProyectoSocio: 1,
    readonly: false,
    duracion: 0,
    empresa: {} as IEmpresa
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioPeriodoJustificacionModalComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: newData },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoSocioPeriodoJustificacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
