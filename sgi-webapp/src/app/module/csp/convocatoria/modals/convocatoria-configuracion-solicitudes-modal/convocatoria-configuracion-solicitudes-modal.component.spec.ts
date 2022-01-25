import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { ConvocatoriaConfiguracionSolicitudesModalComponent, ConvocatoriaConfiguracionSolicitudesModalData } from './convocatoria-configuracion-solicitudes-modal.component';

describe('ConvocatoriaConfiguracionSolicitudesModalComponent', () => {
  let component: ConvocatoriaConfiguracionSolicitudesModalComponent;
  let fixture: ComponentFixture<ConvocatoriaConfiguracionSolicitudesModalComponent>;

  const tipoDocumento: ITipoDocumento = {
    activo: true,
    descripcion: '',
    id: 1,
    nombre: ''
  };

  const documentoRequerido: IDocumentoRequeridoSolicitud = {
    configuracionSolicitudId: 1,
    id: 1,
    observaciones: '',
    tipoDocumento
  };

  const data: ConvocatoriaConfiguracionSolicitudesModalData = {
    documentoRequerido,
    tipoFaseId: 1,
    modeloEjecucionId: 1,
    isConvocatoriaVinculada: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConfiguracionSolicitudesModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        CspSharedModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: data },
        { provide: MAT_DIALOG_DATA, useValue: data },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConfiguracionSolicitudesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
