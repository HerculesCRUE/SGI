import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { Estado } from '@core/models/csp/estado-solicitud';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SOLICITUD_PUBLIC_DATA_KEY } from '../../solicitud-public-data.resolver';
import { ISolicitudPublicData, SolicitudPublicActionService } from '../../solicitud-public.action.service';
import { SolicitudRrhhRequisitosConvocatoriaPublicComponent } from './solicitud-rrhh-requisitos-convocatoria-public.component';

describe('SolicitudRrhhRequisitosConvocatoriaPublicComponent', () => {
  let component: SolicitudRrhhRequisitosConvocatoriaPublicComponent;
  let fixture: ComponentFixture<SolicitudRrhhRequisitosConvocatoriaPublicComponent>;
  const routeData: Data = {
    [SOLICITUD_PUBLIC_DATA_KEY]: {
      solicitud: {
        formularioSolicitud: FormularioSolicitud.RRHH,
        estado: {
          estado: Estado.BORRADOR
        }
      }
    } as ISolicitudPublicData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  const state = {
    idConvocatoria: 1
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudRrhhRequisitosConvocatoriaPublicComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        SolicitudPublicActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(SolicitudRrhhRequisitosConvocatoriaPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
