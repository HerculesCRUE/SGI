import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioPeriodoPagoModalComponent, SolicitudProyectoSocioPeriodoPagoModalData } from './solicitud-proyecto-socio-periodo-pago-modal.component';

describe('SolicitudProyectoPeriodoPagoModalComponent', () => {
  let component: SolicitudProyectoSocioPeriodoPagoModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioPeriodoPagoModalComponent>;

  const solicitudProyectoPeriodoPago: ISolicitudProyectoSocioPeriodoPago = {
    id: 1,
    importe: undefined,
    mes: undefined,
    numPeriodo: undefined,
    solicitudProyectoSocioId: undefined
  };

  const newData: SolicitudProyectoSocioPeriodoPagoModalData = {
    duracion: 5,
    isEdit: false,
    selectedMeses: [],
    solicitudProyectoPeriodoPago,
    mesInicioSolicitudProyectoSocio: 1,
    mesFinSolicitudProyectoSocio: 2,
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioPeriodoPagoModalComponent
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
    fixture = TestBed.createComponent(SolicitudProyectoSocioPeriodoPagoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
