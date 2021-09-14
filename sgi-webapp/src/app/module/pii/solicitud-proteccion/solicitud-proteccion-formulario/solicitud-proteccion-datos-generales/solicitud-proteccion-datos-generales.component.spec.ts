import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IInvencion } from '@core/models/pii/invencion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SOLICITUD_PROTECCION_DATA_KEY } from '../../solicitud-proteccion-data.resolver';
import { ISolicitudProteccionData, SolicitudProteccionActionService } from '../../solicitud-proteccion.action.service';

import { SolicitudProteccionDatosGeneralesComponent } from './solicitud-proteccion-datos-generales.component';

describe('SolicitudProteccionDatosGeneralesComponent', () => {
  let component: SolicitudProteccionDatosGeneralesComponent;
  let fixture: ComponentFixture<SolicitudProteccionDatosGeneralesComponent>;

  const routeData: Data = {
    [SOLICITUD_PROTECCION_DATA_KEY]: {
      invencion: {
        tipoProteccion: {}
      } as IInvencion,
      solicitudesProteccion: []
    } as ISolicitudProteccionData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('0', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SolicitudProteccionDatosGeneralesComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        // SgiAuthModule,
        LoggerTestingModule,
        RouterTestingModule,
        ReactiveFormsModule,
        SharedModule
      ],
      providers: [
        // { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        SolicitudProteccionActionService,
        { provide: ActivatedRoute, useValue: routeMock }
        // SgiAuthService
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
