import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../../../ejecucion-economica/ejecucion-economica-data.resolver';
import { IEjecucionEconomicaData } from '../../../ejecucion-economica/ejecucion-economica.action.service';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { REQUERIMIENTO_JUSTIFICACION_DATA_KEY } from '../../seguimiento-justificacion-requerimiento-data.resolver';
import { IRequerimientoJustificacionData, SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';

import { SeguimientoJustificacionRequerimientoGastosComponent } from './seguimiento-justificacion-requerimiento-gastos.component';

describe('SeguimientoJustificacionRequerimientoGastosComponent', () => {
  let component: SeguimientoJustificacionRequerimientoGastosComponent;
  let fixture: ComponentFixture<SeguimientoJustificacionRequerimientoGastosComponent>;
  const routeData: Data = {
    [REQUERIMIENTO_JUSTIFICACION_DATA_KEY]: {
      incidenciasDocumentacion: [],
      requerimientoJustificacion: {},
      canEdit: true
    } as IRequerimientoJustificacionData
  };
  const parentData: Data = {
    [EJECUCION_ECONOMICA_DATA_KEY]: {
      proyectoSge: { id: '1' }
    } as IEjecucionEconomicaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData, parentData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SeguimientoJustificacionRequerimientoGastosComponent],
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
        CspSharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        SeguimientoJustificacionRequerimientoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoJustificacionRequerimientoGastosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

