import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Estado } from '@core/models/csp/estado-solicitud';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CambioEstadoPublicModalComponent, SolicitudCambioEstadoPublicModalComponentData } from './cambio-estado-public-modal.component';


describe('CambioEstadoPublicModalComponent', () => {
  let component: CambioEstadoPublicModalComponent;
  let fixture: ComponentFixture<CambioEstadoPublicModalComponent>;

  const newData: SolicitudCambioEstadoPublicModalComponentData = {
    estadoActual: Estado.BORRADOR,
    estadoNuevo: undefined,
    fechaEstado: undefined,
    comentario: undefined,
    isInvestigador: undefined,
    solicitudProyecto: undefined,
    hasRequiredDocumentos: true,
    isSolicitanteInSolicitudEquipo: true,
    solicitud: undefined,
    isTutor: false,
    publicId: ''
  };
  const state = {
    idConvocatoria: 1
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CambioEstadoPublicModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        LoggerTestingModule,
        ReactiveFormsModule,
        SharedModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(CambioEstadoPublicModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
