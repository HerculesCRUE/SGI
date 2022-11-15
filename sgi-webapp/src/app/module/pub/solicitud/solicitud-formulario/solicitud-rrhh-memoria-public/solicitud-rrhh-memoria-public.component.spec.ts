import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { Estado } from '@core/models/csp/estado-solicitud';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpPublicSharedModule } from 'src/app/esb/sgp/shared/sgp-public-shared.module';
import { SOLICITUD_PUBLIC_DATA_KEY } from '../../solicitud-public-data.resolver';
import { ISolicitudPublicData, SolicitudPublicActionService } from '../../solicitud-public.action.service';

import { SolicitudRrhhMemoriaPublicComponent } from './solicitud-rrhh-memoria-public.component';

describe('SolicitudRrhhMemoriaPublicComponent', () => {
  let component: SolicitudRrhhMemoriaPublicComponent;
  let fixture: ComponentFixture<SolicitudRrhhMemoriaPublicComponent>;

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
        SolicitudRrhhMemoriaPublicComponent
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
        SharedModule,
        SgpPublicSharedModule
      ],
      providers: [
        SolicitudPublicActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(SolicitudRrhhMemoriaPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
