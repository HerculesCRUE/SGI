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
import { SharedPublicModule } from '@shared/shared-public.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgoPublicSharedModule } from 'src/app/esb/sgo/shared/sgo-public-shared.module';
import { SgpPublicSharedModule } from 'src/app/esb/sgp/shared/sgp-public-shared.module';
import { SOLICITUD_PUBLIC_DATA_KEY } from '../../solicitud-public-data.resolver';
import { ISolicitudPublicData, SolicitudPublicActionService } from '../../solicitud-public.action.service';
import { SolicitudDatosGeneralesPublicComponent } from './solicitud-datos-generales-public.component';

describe('SolicitudDatosGeneralesPublicComponent', () => {
  let component: SolicitudDatosGeneralesPublicComponent;
  let fixture: ComponentFixture<SolicitudDatosGeneralesPublicComponent>;
  const routeData: Data = {
    [SOLICITUD_PUBLIC_DATA_KEY]: {
      solicitud: {
        formularioSolicitud: FormularioSolicitud.PROYECTO,
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
        SolicitudDatosGeneralesPublicComponent
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
        SharedPublicModule,
        SgpPublicSharedModule,
        SgoPublicSharedModule
      ],
      providers: [
        SolicitudPublicActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(SolicitudDatosGeneralesPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
